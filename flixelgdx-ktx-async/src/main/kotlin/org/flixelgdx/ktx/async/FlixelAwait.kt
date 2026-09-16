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
import org.flixelgdx.animation.FlixelAnimationController
import org.flixelgdx.animation.FlixelAnimationFrameSignalData
import org.flixelgdx.tween.FlixelTween
import org.flixelgdx.tween.settings.FlixelTweenSettings
import org.flixelgdx.tween.settings.FlixelTweenType
import org.flixelgdx.util.signal.FlixelSignal

/**
 * Suspends the current coroutine until this tween completes its final cycle.
 *
 * Registration is done via [FlixelTween.then], which fires after the tween's own `onComplete`
 * callback and therefore does not displace any callback already set on [FlixelTweenSettings].
 * If the coroutine is canceled before the tween finishes, the suspension is abandoned but the
 * tween itself continues to run - cancel the tween separately if that behavior is needed.
 *
 * Note that `then` does not fire for [FlixelTweenType.LOOPING] or [FlixelTweenType.PINGPONG]
 * tweens, so this function will suspend indefinitely when called on those types.
 *
 * Example:
 * ```
 * launch {
 *   val tween = sprite.tween(duration = 2.5f) {
 *     goal(sprite::getX, 365f, sprite::setX)
 *   }
 *   tween.await()
 *   Flixel.info("Tween done!")
 * }
 * ```
 */
suspend fun FlixelTween.await(): Unit = suspendCancellableCoroutine { cont ->
  then {
    if (cont.isActive) {
      cont.resume(Unit)
    }
    null
  }
}

/**
 * Suspends the current coroutine until this signal fires once, then resumes with the dispatched
 * value.
 *
 * Uses [FlixelSignal.addOnce] so the handler auto-removes after the first dispatch. If the
 * coroutine is canceled before the signal fires, the handler remains registered but the
 * continuation is already canceled so resuming it has no effect.
 *
 * Example:
 * ```
 * launch {
 *   val data = Flixel.Signals.preStateSwitch.awaitOnce()
 *   Flixel.info("Switching to: " + data.state())
 * }
 * ```
 *
 * @param T The data type carried by this signal.
 * @return The value the signal was dispatched with, or `null` if dispatched via the no-arg
 *   overload.
 */
suspend fun <T> FlixelSignal<T>.awaitOnce(): T = suspendCancellableCoroutine { cont ->
  addOnce { data: T ->
    if (cont.isActive) {
      cont.resume(data)
    }
  }
}

/**
 * Suspends the current coroutine until the named animation finishes playing on this controller.
 *
 * Registers a one-time listener on [FlixelAnimationController.onAnimationFinished]. If the
 * animation that finishes has a different name than [name], the listener stays registered and waits
 * for the next matching finish event. The coroutine resumes with the
 * [FlixelAnimationFrameSignalData] carrying the final frame information.
 *
 * If the coroutine is canceled, the listener remains registered until the named animation
 * finishes, at which point it silently discards the result. This is an unavoidable limitation
 * because [FlixelSignal] does not expose a way to remove a one-time handler after the fact. Call
 * [FlixelAnimationController.onAnimationFinished]'s `remove(...)` with the same handler reference
 * if early removal is needed (not supported by this convenience wrapper).
 *
 * Example:
 * ```
 * launch {
 *   sprite.animation.play("attack")
 *   sprite.animation.await("attack")
 *   sprite.animation.play("idle")
 * }
 * ```
 *
 * @param name The name of the animation to wait for.
 * @return The [FlixelAnimationFrameSignalData] from the final frame of the animation.
 */
suspend fun FlixelAnimationController.await(name: String): FlixelAnimationFrameSignalData =
  suspendCancellableCoroutine { cont ->
    // We need to re-register on every non-matching finish, so use a permanent add + manual remove.
    val handler =
      object : FlixelSignal.SignalHandler<FlixelAnimationFrameSignalData> {
        override fun execute(data: FlixelAnimationFrameSignalData?) {
          if (data != null && data.animationName == name) {
            onAnimationFinished.remove(this)
            if (cont.isActive) {
              cont.resume(data)
            }
          }
        }
      }
    onAnimationFinished.add(handler)
    cont.invokeOnCancellation { onAnimationFinished.remove(handler) }
  }
