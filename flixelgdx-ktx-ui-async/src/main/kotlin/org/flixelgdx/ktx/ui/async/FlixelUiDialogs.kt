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
@file:JvmName("FlixelUiDialogs")

package org.flixelgdx.ktx.ui.async

import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine
import org.flixelgdx.ktx.ui.FlixelUiDsl
import org.flixelgdx.ui.FlixelUiDisplay
import org.flixelgdx.ui.FlixelUiModal

/**
 * Shows a modal built with [block] and suspends until it is finished, returning the result.
 *
 * The block builds the modal's contents with the usual builder DSL and receives a `finish`
 * function. Calling `finish(value)` closes the modal and resumes the caller with `value`. If the
 * modal is closed any other way (for example the game calls `ui.closeModal()` when Escape is
 * pressed), the caller resumes with [dismissed] instead. A confirm box becomes one expression:
 * ```kotlin
 * launch {
 *   val quit = ui.dialog(300f, 160f, dismissed = false) { finish ->
 *     label("Really quit?")
 *     hstack(spacing = 8f) {
 *       button("Yes") { onClick { finish(true) } }
 *       button("No") { onClick { finish(false) } }
 *     }
 *   }
 *   if (quit) exitGame()
 * }
 * ```
 *
 * `finish` is a parameter rather than a function on the modal so it can be called from inside
 * nested builder blocks, where the DSL marker hides the modal's own members. Only the first call to
 * `finish` counts. The modal is destroyed once it closes. Canceling the coroutine closes the modal;
 * cancel from the game thread, as the KTX dispatcher does.
 *
 * @param width The modal's width in pixels.
 * @param height The modal's height in pixels.
 * @param dismissed The result when the modal closes without `finish(...)` being called.
 * @param style The modal style name to use, or `null` for `"default"`.
 * @param block Builds the modal's contents and wires its buttons to `finish`.
 * @return The value passed to `finish`, or [dismissed].
 */
suspend fun <R> FlixelUiDisplay.dialog(
  width: Float,
  height: Float,
  dismissed: R,
  style: String? = null,
  block: @FlixelUiDsl FlixelUiModal.(finish: (R) -> Unit) -> Unit,
): R {
  val modal = FlixelUiModal(width, height)
  modal.destroyOnClose = true
  if (style != null) {
    modal.styleName = style
  }
  return suspendCancellableCoroutine { cont ->
    var done = false
    val finish: (R) -> Unit = { value ->
      if (!done) {
        done = true
        modal.close()
        if (cont.isActive) {
          cont.resume(value)
        }
      }
    }
    modal.onClose.add {
      if (!done) {
        done = true
        if (cont.isActive) {
          cont.resume(dismissed)
        }
      }
    }
    cont.invokeOnCancellation {
      if (!done) {
        done = true
        modal.close()
      }
    }
    modal.block(finish)
    if (done) {
      // finish(...) was called while the contents were still being built.
      modal.destroy()
    } else {
      openModal(modal)
    }
  }
}
