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
@file:JvmName("FlixelUiAwait")

package org.flixelgdx.ktx.ui.async

import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine
import org.flixelgdx.ui.FlixelUiButton
import org.flixelgdx.ui.FlixelUiCheckbox
import org.flixelgdx.ui.FlixelUiDropdown
import org.flixelgdx.ui.FlixelUiModal
import org.flixelgdx.ui.FlixelUiRadioGroup
import org.flixelgdx.ui.text.FlixelTextBox
import org.flixelgdx.util.signal.FlixelSignal
import org.flixelgdx.util.signal.FlixelSignal.SignalHandler

/**
 * Suspending functions that wait for the player to use a widget.
 *
 * Each one suspends until the widget's signal fires once, then resumes with the new value, so a
 * small interaction reads top to bottom instead of being split across callbacks:
 * ```kotlin
 * launch {
 *   val name = nameBox.awaitSubmit()
 *   greeting.text = "Hello, $name!"
 *   continueButton.awaitClick()
 *   startGame()
 * }
 * ```
 *
 * The value is read when the signal fires, not when the coroutine resumes on the next dispatcher
 * pass, so it is always the value the player chose. When the coroutine is canceled first, the
 * waiting handler is removed from the signal. Cancel from the game thread, as the KTX dispatcher
 * does.
 */

/** Suspends until this button is clicked. */
suspend fun FlixelUiButton.awaitClick() {
  onClick.awaitMapped {}
}

/**
 * Suspends until this checkbox is checked or unchecked.
 *
 * @return Whether the checkbox is checked after the change.
 */
suspend fun FlixelUiCheckbox.awaitChange(): Boolean = onChange.awaitMapped { it.isChecked }

/**
 * Suspends until the selection of this radio group changes.
 *
 * @return The index of the newly selected radio button.
 */
suspend fun FlixelUiRadioGroup.awaitChange(): Int = onChange.awaitMapped { it.selectedIndex }

/**
 * Suspends until the player selects a different item in this dropdown.
 *
 * @return The index of the newly selected item.
 */
suspend fun FlixelUiDropdown.awaitSelect(): Int = onSelect.awaitMapped { it.selectedIndex }

/**
 * Suspends until the player submits this text box.
 *
 * @return A copy of the submitted text.
 */
suspend fun FlixelTextBox.awaitSubmit(): String = onSubmit.awaitMapped { it.text.toString() }

/** Suspends until this modal closes; returns right away when it is not open. */
suspend fun FlixelUiModal.awaitClose() {
  if (!isOpen) {
    return
  }
  onClose.awaitMapped {}
}

/**
 * Suspends until this signal fires once and resumes with [map] applied to the dispatched value.
 *
 * Unlike `awaitOnce()`, the handler is removed again when the coroutine is canceled first.
 */
private suspend inline fun <T, R> FlixelSignal<T>.awaitMapped(crossinline map: (T) -> R): R =
  suspendCancellableCoroutine { cont ->
    val handler =
      SignalHandler<T> { data ->
        if (cont.isActive) {
          cont.resume(map(data))
        }
      }
    addOnce(handler)
    cont.invokeOnCancellation { remove(handler) }
  }
