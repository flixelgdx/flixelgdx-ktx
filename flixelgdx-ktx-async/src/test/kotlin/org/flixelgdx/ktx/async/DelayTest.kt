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
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.flixelgdx.FlixelBasic
import org.flixelgdx.graphics.FlixelBatch
import org.flixelgdx.util.timer.FlixelTimer
import org.flixelgdx.util.timer.FlixelTimerManager

/**
 * Tests for [delay] coroutine plumbing.
 *
 * NOTE: [delay] internally creates a [FlixelTimer] on the global [FlixelTimerManager]. The
 * timer manager extends [FlixelBasic] and depends on [FlixelBatch] and the full game loop, so it
 * cannot be driven headlessly in unit tests. These tests therefore exercise the coroutine
 * continuation-resume wiring directly: they capture the continuation that
 * [suspendCancellableCoroutine] produces and fire it manually, verifying that the code after
 * [delay] runs only after the resume and that cancellation is honored.
 *
 * For full integration coverage (actual time elapsing frame-by-frame), run a test project that
 * consumes the framework via a composite build or JitPack.
 */
class DelayTest {

  /**
   * Verifies that a coroutine suspended at a suspension point does not advance until explicitly
   * resumed.
   *
   * Uses a plain [suspendCoroutine] (not [delay] directly, because the real [FlixelTimer]
   * cannot run headlessly). This exercises the same continuation-dispatch path that [delay]
   * relies on.
   */
  @Test
  fun continuationResumesOnlyWhenExplicitlyResumed() {
    var afterDelay = false
    var capturedCont: Continuation<Unit>? = null

    scope.launch(Dispatcher) {
      suspendCoroutine<Unit> { cont -> capturedCont = cont }
      afterDelay = true
    }

    // First pump: starts the coroutine and parks it at the suspension point.
    Dispatcher.update()
    assertFalse(afterDelay, "Flag must not be set before the continuation is resumed")
    assertTrue(capturedCont != null, "Continuation should have been captured")

    // Simulate the timer callback firing: resume the continuation.
    capturedCont!!.resume(Unit)

    // The resumed block is dispatched back to Dispatcher - pump again.
    Dispatcher.update()
    assertTrue(
      afterDelay,
      "Flag should be set after the continuation is resumed and dispatcher pumped",
    )
  }

  /**
   * Verifies that cancelling a coroutine before the timer fires prevents the post-delay code from
   * running, mirroring the behavior that [delay] achieves via
   * [CancellableContinuation.invokeOnCancellation].
   */
  @Test
  fun cancelledCoroutineDoesNotRunPostDelayCode() {
    var afterDelay = false

    val job: Job =
      scope.launch(Dispatcher) {
        // Park on a cancellable continuation exactly as delay does. It is never resumed
        // normally here, mirroring a state switch cancelling the coroutine before the timer fires.
        suspendCancellableCoroutine<Unit> {}
        afterDelay = true
      }

    // Pump so the coroutine starts and parks.
    Dispatcher.update()
    assertFalse(afterDelay)
    assertTrue(job.isActive)

    // Cancel the scope's children, as installFlixelCoroutines does on a state switch, then pump
    // to deliver the cancellation. The post-delay code must never run.
    scope.coroutineContext.cancelChildren()
    Dispatcher.update()

    assertFalse(job.isActive, "Job should no longer be active after cancel")
    assertFalse(afterDelay, "Post-delay code must not run when the coroutine was cancelled")
  }
}
