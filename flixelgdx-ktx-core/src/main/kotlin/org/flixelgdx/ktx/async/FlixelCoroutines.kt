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

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.Continuation
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.startCoroutine
import org.flixelgdx.Flixel
import org.flixelgdx.collections.FlixelArray

/**
 * Runs resumed coroutines on the game thread, one dispatcher pass per frame.
 *
 * Safety model: all coroutines started with [launch] execute on the same thread that drives the
 * game loop. Each suspension point slices execution between frames. A coroutine that never suspends
 * will freeze the entire frame, so always use [delay] or other suspension points for long-running
 * work. Launch new coroutines once per game sequence (for example inside a state's `create()`
 * method) rather than every frame; each [launch] allocates a coroutine object.
 *
 * The dispatcher is built only on the coroutine support in the Kotlin standard library. It uses no
 * threads, locks, or atomics, so it behaves the same on every platform, including web builds.
 *
 * Example of a simple timed sequence started once in `create()`:
 * ```
 * installFlixelCoroutines()
 * launch {
 *   Flixel.info("Level started!")
 *   delay(3f)
 *   Flixel.info("3 seconds later...")
 * }
 * ```
 */
object Dispatcher :
  AbstractCoroutineContextElement(ContinuationInterceptor), ContinuationInterceptor {

  /** Blocks waiting for the next [update]. */
  private var queue: FlixelArray<Runnable> = FlixelArray()

  /** The blocks of the pass in progress; swapped with [queue] so new blocks wait a pass. */
  private var running: FlixelArray<Runnable> = FlixelArray()

  override fun <T> interceptContinuation(continuation: Continuation<T>): Continuation<T> =
    GameThreadContinuation(continuation)

  internal fun dispatch(block: Runnable) {
    queue.add(block)
  }

  /**
   * Runs every block queued since the last pass, each exactly once.
   *
   * This is called automatically each frame when [installFlixelCoroutines] has been called. Blocks
   * queued during the pass run on the next pass, so a coroutine that resumes another one cannot
   * make this loop run forever. If a coroutine throws, the exception propagates out of this call
   * and the blocks that did not run yet are kept for the next pass.
   */
  fun update() {
    if (queue.size == 0) {
      return
    }
    val batch = queue
    queue = running
    running = batch
    var index = 0
    try {
      while (index < batch.size) {
        val block = batch[index]
        index++
        block.run()
      }
    } finally {
      for (i in index until batch.size) {
        queue.add(batch[i])
      }
      batch.clear()
    }
  }
}

/**
 * Wraps a coroutine's continuation so every resumption waits in the [Dispatcher] queue.
 *
 * When the coroutine's job was canceled in the meantime, a successful resumption is turned into a
 * cancellation, so a canceled coroutine never runs more of its body than its cleanup.
 */
private class GameThreadContinuation<T>(private val continuation: Continuation<T>) :
  Continuation<T>, Runnable {

  private var value: Any? = null
  private var error: Throwable? = null

  override val context: CoroutineContext
    get() = continuation.context

  override fun resumeWith(result: Result<T>) {
    value = result.getOrNull()
    error = result.exceptionOrNull()
    Dispatcher.dispatch(this)
  }

  override fun run() {
    val job = continuation.context[FlixelJob]
    val failure = error ?: if (job != null && job.isCancelled) job.cancellationException() else null
    val result = value
    value = null
    error = null
    if (failure != null) {
      continuation.resumeWith(Result.failure(failure))
    } else {
      // The value came from a Result<T>, so the cast only restores the erased type.
      @Suppress("UNCHECKED_CAST") continuation.resumeWith(Result.success(result as T))
    }
  }
}

private var installed: Boolean = false

/**
 * Wires [Dispatcher] into the game loop exactly once.
 *
 * Subscribes [Dispatcher.update] to [Flixel.Signals.postUpdate] so that resumed coroutines run
 * every frame. Also subscribes [cancelAllCoroutines] to [Flixel.Signals.preStateSwitch], so
 * coroutines never leak from one state into the next. Coroutines launched in the next state run
 * normally without calling this function again.
 *
 * Calling this more than once is safe; subsequent calls are no-ops.
 */
fun installFlixelCoroutines() {
  if (installed) {
    return
  }
  installed = true
  Flixel.Signals.postUpdate.add { Dispatcher.update() }
  Flixel.Signals.preStateSwitch.add { cancelAllCoroutines() }
}

/**
 * Launches a new coroutine that runs on the game thread.
 *
 * The body starts on the next [Dispatcher] pass, not inside this call. Do not call this per frame;
 * each invocation allocates a new coroutine object. Launch sequences once (for example inside a
 * state's `create()` method) and let suspension points slice work across frames.
 *
 * If the body throws an exception other than a cancellation, the exception propagates out of the
 * [Dispatcher] pass, so it reaches the game's crash handler just like an exception thrown from
 * `update()`.
 *
 * @param block The coroutine body. Its receiver is the coroutine's own [FlixelJob].
 * @return The [FlixelJob] representing the launched coroutine.
 */
fun launch(block: suspend FlixelJob.() -> Unit): FlixelJob {
  val job = FlixelJob()
  trackJob(job)
  block.startCoroutine(job, job.completion)
  return job
}
