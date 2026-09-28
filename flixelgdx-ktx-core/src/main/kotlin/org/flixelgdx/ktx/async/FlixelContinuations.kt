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

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.intercepted
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn

/**
 * The continuation handed to [suspendCancellable], resumed once when the awaited event happens.
 *
 * Resume it with `resume(value)` from `kotlin.coroutines`. Only the first resume counts; later
 * calls, and calls after the coroutine was canceled, are ignored. A resume that happens while the
 * setup block is still running returns the value from [suspendCancellable] right away. Any later
 * resume goes through the [Dispatcher], so the coroutine continues on the next dispatcher pass
 * rather than inside the callback that resumed it.
 *
 * @param T The type of the value the suspended coroutine resumes with.
 */
class FlixelCancellableContinuation<in T>
@PublishedApi
internal constructor(uCont: Continuation<T>) : Continuation<T> {

  private val delegate: Continuation<T> = uCont.intercepted()

  private val job: FlixelJob? = uCont.context[FlixelJob]

  private var cancelHandler: (() -> Unit)? = null

  /** False while the setup block of [suspendCancellable] is still running. */
  private var parked: Boolean = false

  /** Set when the continuation was resumed or canceled before the setup block returned. */
  private var resumedEarly: Boolean = false
  private var earlyValue: Any? = null
  private var earlyError: Throwable? = null

  override val context: CoroutineContext
    get() = delegate.context

  /** Whether the coroutine is still waiting here, meaning it was neither resumed nor canceled. */
  var isActive: Boolean = true
    private set

  /** Whether the coroutine was canceled while it was waiting here. */
  var isCancelled: Boolean = false
    private set

  /**
   * Registers [handler] to run if the coroutine is canceled while it waits here.
   *
   * Use it to undo whatever the suspension set up, such as removing a signal handler or canceling a
   * timer. The handler runs on the game thread, at the moment [FlixelJob.cancel] is called. Only
   * one handler is kept; registering another replaces it. If the coroutine was already canceled,
   * [handler] runs right away.
   *
   * @param handler The cleanup to run on cancellation.
   */
  fun invokeOnCancellation(handler: () -> Unit) {
    if (isCancelled) {
      handler()
    } else if (isActive) {
      cancelHandler = handler
    }
  }

  override fun resumeWith(result: Result<T>) {
    if (!isActive) {
      return
    }
    isActive = false
    cancelHandler = null
    detachFromJob()
    if (parked) {
      delegate.resumeWith(result)
    } else {
      resumedEarly = true
      earlyValue = result.getOrNull()
      earlyError = result.exceptionOrNull()
    }
  }

  internal fun cancel(cause: CancellationException) {
    if (!isActive) {
      return
    }
    isActive = false
    isCancelled = true
    detachFromJob()
    val handler = cancelHandler
    cancelHandler = null
    handler?.invoke()
    if (parked) {
      delegate.resumeWith(Result.failure(cause))
    } else {
      resumedEarly = true
      earlyError = cause
    }
  }

  /**
   * Parks the coroutine after the setup block has run.
   *
   * @return [COROUTINE_SUSPENDED], or the value if the continuation was resumed during setup.
   * @throws Throwable The failure the continuation was resumed with during setup, such as a
   *   [CancellationException] when the job was already canceled.
   */
  @PublishedApi
  internal fun suspend(): Any? {
    val job = job
    if (job != null && isActive) {
      if (job.isCancelled) {
        cancel(job.cancellationException())
      } else {
        job.suspension = this
      }
    }
    parked = true
    if (!resumedEarly) {
      return COROUTINE_SUSPENDED
    }
    val error = earlyError
    if (error != null) {
      throw error
    }
    return earlyValue
  }

  private fun detachFromJob() {
    val job = job
    if (job != null && job.suspension === this) {
      job.suspension = null
    }
  }
}

/**
 * Suspends the current coroutine until [block] arranges for it to be resumed.
 *
 * This is the building block behind [delay] and every `await` function. [block] runs right away
 * with a [FlixelCancellableContinuation]; hand that continuation to a callback (a signal handler, a
 * timer, a tween) that calls `resume(value)` when the event happens, and register cleanup with
 * [FlixelCancellableContinuation.invokeOnCancellation]:
 * ```
 * suspend fun FlixelSignal<Int>.awaitScore(): Int = suspendCancellable { cont ->
 *   val handler = SignalHandler<Int> { cont.resume(it) }
 *   addOnce(handler)
 *   cont.invokeOnCancellation { remove(handler) }
 * }
 * ```
 *
 * If the coroutine's [FlixelJob] is canceled while it waits, the cleanup runs and the coroutine
 * throws a [CancellationException] from this call.
 *
 * @param T The type of the value the coroutine resumes with.
 * @param block Sets up the callback that resumes the continuation.
 * @return The value the continuation was resumed with.
 * @throws CancellationException If the coroutine is canceled before it is resumed.
 */
suspend inline fun <T> suspendCancellable(
  crossinline block: (FlixelCancellableContinuation<T>) -> Unit
): T = suspendCoroutineUninterceptedOrReturn { uCont ->
  val cont = FlixelCancellableContinuation(uCont)
  block(cont)
  cont.suspend()
}
