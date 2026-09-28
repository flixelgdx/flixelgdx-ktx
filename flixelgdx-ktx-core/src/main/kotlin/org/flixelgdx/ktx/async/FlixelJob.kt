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
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.cancellation.CancellationException

/**
 * A handle to a coroutine started with [launch].
 *
 * The job is also the receiver of the coroutine body, so the body can check [isActive] or call
 * [cancel] on itself directly:
 * ```
 * launch {
 *   while (isActive) {
 *     spawnEnemy()
 *     delay(2f)
 *   }
 * }
 * ```
 *
 * Canceling a job makes the coroutine throw a [CancellationException] at its current (or next)
 * suspension point, so `finally` blocks still run. Every job belongs to the game thread; create,
 * cancel, and read it from there only.
 */
class FlixelJob internal constructor() : AbstractCoroutineContextElement(FlixelJob) {

  /** The key used to look up the job in a coroutine's context. */
  companion object Key : CoroutineContext.Key<FlixelJob>

  /** Whether the coroutine has finished, either normally, with an exception, or by cancellation. */
  var isCompleted: Boolean = false
    private set

  /** Whether [cancel] was called before the coroutine finished. */
  var isCancelled: Boolean = false
    private set

  /** Whether the coroutine is still running and has not been canceled. */
  val isActive: Boolean
    get() = !isCompleted && !isCancelled

  /** The context the coroutine runs with: this job and the game thread [Dispatcher]. */
  internal val coroutineContext: CoroutineContext = this + Dispatcher

  /** Receives the coroutine's final result once its body returns or throws. */
  internal val completion: Continuation<Unit> =
    object : Continuation<Unit> {
      override val context: CoroutineContext
        get() = coroutineContext

      override fun resumeWith(result: Result<Unit>) {
        complete(result.exceptionOrNull())
      }
    }

  /** The cancellable suspension the coroutine is parked at, if any. */
  internal var suspension: FlixelCancellableContinuation<*>? = null

  /** Created only when the job is canceled, so running jobs never allocate an exception. */
  private var cancellation: CancellationException? = null

  internal var previous: FlixelJob? = null
  internal var next: FlixelJob? = null

  /**
   * Cancels the coroutine.
   *
   * If the coroutine is parked at a cancellable suspension point (such as [delay] or any of the
   * `await` functions), that point's cleanup runs right away and the coroutine resumes with a
   * [CancellationException] on the next [Dispatcher] pass. Calling this on a job that is already
   * canceled or completed does nothing.
   */
  fun cancel() {
    if (!isActive) {
      return
    }
    isCancelled = true
    val current = suspension
    suspension = null
    current?.cancel(cancellationException())
  }

  internal fun cancellationException(): CancellationException =
    cancellation ?: CancellationException("The coroutine was canceled.").also { cancellation = it }

  private fun complete(error: Throwable?) {
    isCompleted = true
    suspension = null
    untrackJob(this)
    if (error is CancellationException) {
      isCancelled = true
    } else if (error != null) {
      // Surface the failure on the game thread, exactly like an exception thrown from update().
      throw error
    }
  }
}

private var firstJob: FlixelJob? = null
private var lastJob: FlixelJob? = null

internal fun trackJob(job: FlixelJob) {
  val last = lastJob
  job.previous = last
  job.next = null
  if (last == null) {
    firstJob = job
  } else {
    last.next = job
  }
  lastJob = job
}

internal fun untrackJob(job: FlixelJob) {
  val previous = job.previous
  val next = job.next
  if (previous == null) {
    if (firstJob === job) {
      firstJob = next
    }
  } else {
    previous.next = next
  }
  if (next == null) {
    if (lastJob === job) {
      lastJob = previous
    }
  } else {
    next.previous = previous
  }
  job.previous = null
  job.next = null
}

/**
 * Cancels every coroutine started with [launch] that is still running.
 *
 * [installFlixelCoroutines] calls this automatically whenever the state switches. Coroutines
 * launched while the cancellation is in progress (for example from a cleanup handler) are not
 * canceled.
 */
fun cancelAllCoroutines() {
  val last = lastJob ?: return
  var job = firstJob
  while (job != null) {
    val next = job.next
    job.cancel()
    if (job === last) {
      break
    }
    job = next
  }
}
