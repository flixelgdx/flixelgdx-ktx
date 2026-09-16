/*
 * MIT License
 *
 * Copyright (c) 2026 stringdotjar
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.flixelgdx.ktx.async

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import org.flixelgdx.Flixel
import org.flixelgdx.collections.FlixelArray

/**
 * A [CoroutineDispatcher] that runs enqueued continuations on the game thread, one frame at a time.
 *
 * Safety model: all coroutines dispatched here execute on the same thread that drives the game
 * loop. Each suspension point slices execution between frames. A coroutine that never suspends will
 * freeze the entire frame, so always use [delay] or other suspension points for long-running
 * work. Launch new coroutines once per game sequence (for example inside a state's `create()`
 * method) rather than every frame; each [launch] allocates a coroutine object.
 *
 * The scope is shared per [scope] and auto-cancels its children when a state switch fires,
 * keeping the scope itself alive for reuse in the next state.
 *
 * Example - a simple timed sequence started once in create():
 *
 * ```
 * installFlixelCoroutines()
 * launch {
 *   Flixel.info("Level started!")
 *   delay(3f)
 *   Flixel.info("3 seconds later...")
 * }
 * ```
 */
object Dispatcher : CoroutineDispatcher() {

  private val queue: FlixelArray<Runnable> = FlixelArray()

  /**
   * Blocks queued during a drain are held here so they start next frame.
   */
  private val nextQueue: FlixelArray<Runnable> = FlixelArray()

  /**
   * True while drain() is executing, so newly dispatched blocks go to nextQueue.
   */
  private var draining: Boolean = false

  override fun dispatch(context: CoroutineContext, block: Runnable) {
    if (draining) {
      nextQueue.add(block)
    } else {
      queue.add(block)
    }
  }

  /**
   * Drains all queued continuations, running each one exactly once.
   *
   * This is called automatically each frame when [installFlixelCoroutines] has been called.
   * Blocks enqueued during the drain run on the next frame to prevent re-entrancy from executing in
   * the same drain pass.
   */
  fun update() {
    if (queue.size == 0) {
      return
    }
    draining = true
    val size = queue.size
    for (i in 0 until size) {
      queue[i].run()
    }
    queue.clear()
    draining = false

    // Move any blocks that arrived during the drain into the main queue for next frame.
    val nextSize = nextQueue.size
    if (nextSize > 0) {
      for (i in 0 until nextSize) {
        queue.add(nextQueue[i])
      }
      nextQueue.clear()
    }
  }
}

/**
 * The shared [CoroutineScope] for all game-thread coroutines.
 *
 * Built on [Dispatcher] with a [SupervisorJob] so individual child failures do not cancel
 * other siblings. Children are canceled on each state switch (via [installFlixelCoroutines]) so
 * the scope itself remains reusable across states.
 */
val scope: CoroutineScope = CoroutineScope(Dispatcher + SupervisorJob())

private var installed: Boolean = false

/**
 * Wires [Dispatcher] into the game loop exactly once.
 *
 * Subscribes [Dispatcher.update] to [Flixel.Signals.postUpdate] so that queued
 * continuations are pumped every frame. Also subscribes to [Flixel.Signals.preStateSwitch] to
 * cancel all children of [scope], preventing coroutines from leaking across states. The scope
 * itself is not canceled, so new coroutines can be launched in the next state without calling this
 * function again.
 *
 * Calling this more than once is safe; subsequent calls are no-ops.
 */
fun installFlixelCoroutines() {
  if (installed) {
    return
  }
  installed = true
  Flixel.Signals.postUpdate.add {
    Dispatcher.update()
  }
  Flixel.Signals.preStateSwitch.add {
    scope.coroutineContext.cancelChildren()
  }
}

/**
 * Launches a new coroutine in [scope] that runs on the game thread.
 *
 * This is a convenience wrapper for [CoroutineScope.launch] on [scope]. Do not call this
 * per-frame; each invocation allocates a new coroutine object. Launch sequences once (for example
 * inside a state's `create()` method) and let suspension points slice work across frames.
 *
 * @param block The coroutine body to execute.
 * @return The [Job] representing the launched coroutine.
 */
fun launch(block: suspend CoroutineScope.() -> Unit): Job = scope.launch(block = block)
