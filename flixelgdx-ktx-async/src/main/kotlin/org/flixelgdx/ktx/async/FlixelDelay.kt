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

import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine
import org.flixelgdx.Flixel
import org.flixelgdx.util.timer.FlixelTimer

/**
 * Suspends the current coroutine for the given number of in-game seconds.
 *
 * The delay is backed by a [FlixelTimer] on the global timer manager. If the coroutine is cancelled
 * while the delay is running, the timer is cancelled via [FlixelTimer.cancel] so it does not fire a
 * stale callback. Time is scaled by [Flixel.timeScale] (because the timer manager respects it), so
 * pausing or slowing down the game also affects this delay.
 *
 * Example:
 * ```
 * launch {
 *   showBanner("Get ready!")
 *   delay(3f)
 *   hideBanner()
 * }
 * ```
 *
 * @param seconds The number of in-game seconds to wait. Values less than or equal to zero fire on
 *   the next update frame, matching [FlixelTimer] zero-duration behavior.
 */
suspend fun delay(seconds: Float): Unit = suspendCancellableCoroutine { cont ->
  val timer = FlixelTimer()
  timer.start(seconds, { cont.resume(Unit) }, 1)
  cont.invokeOnCancellation { timer.cancel() }
}
