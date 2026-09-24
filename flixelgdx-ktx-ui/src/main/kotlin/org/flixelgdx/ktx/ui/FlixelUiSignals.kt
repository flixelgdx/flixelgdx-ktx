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
@file:JvmName("FlixelUiSignals")

package org.flixelgdx.ktx.ui

import org.flixelgdx.ui.FlixelUiButton
import org.flixelgdx.ui.FlixelUiCheckbox
import org.flixelgdx.ui.FlixelUiDropdown
import org.flixelgdx.ui.FlixelUiModal
import org.flixelgdx.ui.FlixelUiRadioButton
import org.flixelgdx.ui.FlixelUiRadioGroup
import org.flixelgdx.ui.text.FlixelTextBox
import org.flixelgdx.util.signal.FlixelSignal.SignalHandler

/**
 * Short handler registration for the signals of FlixelGDX UI widgets.
 *
 * Each function adds [handler] to the matching public signal, so `onClick { startGame() }` inside a
 * `button { }` block reads the same as `onClick.add { startGame() }`. The handler receives the
 * widget that fired the signal.
 *
 * These functions return the handler they registered. Keep it in a variable when the handler must
 * be removed later with `-=` (from `org.flixelgdx.ktx.signal`), since a new lambda written at the
 * removal site is a different object.
 */

/**
 * Registers [handler] to run whenever this button is clicked.
 *
 * @param handler Runs with this button on every click.
 * @return The registered handler.
 */
fun FlixelUiButton.onClick(handler: SignalHandler<FlixelUiButton>): SignalHandler<FlixelUiButton> {
  onClick.add(handler)
  return handler
}

/**
 * Registers [handler] to run whenever this checkbox is checked or unchecked.
 *
 * @param handler Runs with this checkbox after each change; read `isChecked` for the new value.
 * @return The registered handler.
 */
fun FlixelUiCheckbox.onChange(
  handler: SignalHandler<FlixelUiCheckbox>
): SignalHandler<FlixelUiCheckbox> {
  onChange.add(handler)
  return handler
}

/**
 * Registers [handler] to run whenever this radio button is selected or deselected.
 *
 * @param handler Runs with this radio button after each change.
 * @return The registered handler.
 */
fun FlixelUiRadioButton.onChange(
  handler: SignalHandler<FlixelUiRadioButton>
): SignalHandler<FlixelUiRadioButton> {
  onChange.add(handler)
  return handler
}

/**
 * Registers [handler] to run whenever the selection of this group changes.
 *
 * @param handler Runs with this group after each change; read `selectedIndex` for the new value.
 * @return The registered handler.
 */
fun FlixelUiRadioGroup.onChange(
  handler: SignalHandler<FlixelUiRadioGroup>
): SignalHandler<FlixelUiRadioGroup> {
  onChange.add(handler)
  return handler
}

/**
 * Registers [handler] to run whenever the player selects a different item.
 *
 * @param handler Runs with this dropdown after each selection; read `selectedIndex` or
 *   `selectedItem` for the new value.
 * @return The registered handler.
 */
fun FlixelUiDropdown.onSelect(
  handler: SignalHandler<FlixelUiDropdown>
): SignalHandler<FlixelUiDropdown> {
  onSelect.add(handler)
  return handler
}

/**
 * Registers [handler] to run whenever the text in this box changes.
 *
 * @param handler Runs with this text box after each edit.
 * @return The registered handler.
 */
fun FlixelTextBox.onChange(handler: SignalHandler<FlixelTextBox>): SignalHandler<FlixelTextBox> {
  onChange.add(handler)
  return handler
}

/**
 * Registers [handler] to run whenever the player submits this text box (for example with Enter).
 *
 * @param handler Runs with this text box on each submit.
 * @return The registered handler.
 */
fun FlixelTextBox.onSubmit(handler: SignalHandler<FlixelTextBox>): SignalHandler<FlixelTextBox> {
  onSubmit.add(handler)
  return handler
}

/**
 * Registers [handler] to run whenever this modal closes.
 *
 * @param handler Runs with this modal after it closes.
 * @return The registered handler.
 */
fun FlixelUiModal.onClose(handler: SignalHandler<FlixelUiModal>): SignalHandler<FlixelUiModal> {
  onClose.add(handler)
  return handler
}
