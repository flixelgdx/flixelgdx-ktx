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
@file:JvmName("FlixelVideoCoroutines")

package org.flixelgdx.ktx.video

import kotlin.coroutines.resume
import org.flixelgdx.Flixel
import org.flixelgdx.ktx.async.FlixelJob
import org.flixelgdx.ktx.async.cancelAllCoroutines
import org.flixelgdx.ktx.async.suspendCancellable
import org.flixelgdx.signal.FlixelSignal
import org.flixelgdx.signal.FlixelSignalData.UpdateSignalData
import org.flixelgdx.video.FlixelVideo

/**
 * Suspends the current coroutine until this video finishes playing.
 *
 * Waits for [FlixelVideo.onComplete], which the video dispatches once when a non-looping video
 * reaches its end. If the video has already ended, this returns right away. The listener is removed
 * when the video completes or when the coroutine is canceled, so canceling never leaves a stale
 * handler behind.
 *
 * A looping video never completes, so this suspends until the coroutine is canceled. If the video
 * is destroyed before it ends, [FlixelVideo.onComplete] is cleared and never fires, so the
 * coroutine also stays suspended until its [FlixelJob] is canceled (for example by the state switch
 * that [cancelAllCoroutines] runs on).
 *
 * Example:
 * ```
 * launch {
 *   intro.play()
 *   intro.await()
 *   Flixel.switchState { MenuState() }
 * }
 * ```
 */
suspend fun FlixelVideo.await() {
  if (!isLooped && player.isEnded) {
    return
  }
  awaitComplete()
}

/**
 * Starts this video and suspends the current coroutine until it finishes playing.
 *
 * Calls [FlixelVideo.play] and then waits for [FlixelVideo.onComplete], the same way [await] does.
 * Unlike [await], this never returns early because of a previous playthrough: it always waits for
 * the playback it just started to end.
 *
 * Example:
 * ```
 * launch {
 *   cutscene.playAndAwait()
 *   cutscene.destroy()
 *   startLevel()
 * }
 * ```
 *
 * @param forceRestart Whether to restart from the beginning if the video is already playing;
 *   defaults to `true`, matching [FlixelVideo.play] with no arguments.
 */
suspend fun FlixelVideo.playAndAwait(forceRestart: Boolean = true) {
  play(forceRestart)
  awaitComplete()
}

/**
 * Suspends the current coroutine until this video is ready to display.
 *
 * A video's width, height, and length are `0` until [FlixelVideo.isReady] returns `true`, which
 * happens shortly after the first [FlixelVideo.play]. Waiting for it is useful for layout that
 * depends on the video's size, such as centering it. If the video is already ready, this returns
 * right away.
 *
 * The check runs once per frame after the game updates (on [Flixel.Signals.postUpdate]) and does
 * not allocate while it waits. The listener is removed when the video becomes ready or when the
 * coroutine is canceled.
 *
 * Example:
 * ```
 * launch {
 *   video.play()
 *   video.awaitReady()
 *   video.screenCenter()
 * }
 * ```
 */
suspend fun FlixelVideo.awaitReady() {
  if (isReady) {
    return
  }
  suspendCancellable<Unit> { cont ->
    val signal = Flixel.Signals.postUpdate
    val poll =
      object : FlixelSignal.SignalHandler<UpdateSignalData> {
        override fun execute(data: UpdateSignalData?) {
          if (isReady) {
            signal.remove(this)
            if (cont.isActive) {
              cont.resume(Unit)
            }
          }
        }
      }
    signal.add(poll)
    cont.invokeOnCancellation { signal.remove(poll) }
  }
}

// Waits for the next onComplete dispatch, removing the listener on completion or cancellation.
private suspend fun FlixelVideo.awaitComplete(): Unit = suspendCancellable { cont ->
  val handler =
    object : FlixelSignal.SignalHandler<Void> {
      override fun execute(data: Void?) {
        onComplete.remove(this)
        if (cont.isActive) {
          cont.resume(Unit)
        }
      }
    }
  onComplete.add(handler)
  cont.invokeOnCancellation { onComplete.remove(handler) }
}
