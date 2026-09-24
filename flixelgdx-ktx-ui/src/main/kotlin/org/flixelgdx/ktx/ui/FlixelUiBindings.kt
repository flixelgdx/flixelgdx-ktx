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
@file:JvmName("FlixelUiBindings")

package org.flixelgdx.ktx.ui

import kotlin.reflect.KMutableProperty0
import org.flixelgdx.ui.FlixelUiCheckbox
import org.flixelgdx.ui.FlixelUiDropdown
import org.flixelgdx.ui.FlixelUiRadioGroup
import org.flixelgdx.ui.text.FlixelTextBox

/**
 * Two-way bindings between widgets and Kotlin properties.
 *
 * A settings screen is mostly "show this value, and write it back when the player changes it". Each
 * `bind(...)` does both: it shows the property's current value in the widget without firing the
 * widget's change signal, then registers a handler that writes every change back. Combined with the
 * `save()` delegate, a settings screen needs no glue code at all:
 * ```kotlin
 * class Settings {
 *   var fullscreen by save(false)
 *   var playerName by save("Hero")
 *   var quality by save(1)
 * }
 *
 * checkbox("Fullscreen").bind(settings::fullscreen)
 * textBox(200f).bind(settings::playerName)
 * dropdown(160f) { items("Low", "Medium", "High") }.bind(settings::quality)
 * ```
 *
 * Bindings only call the property's getter and setter; they do not use reflection. Changing the
 * property from code afterwards does not update the widget, so bind again or set the widget
 * directly when that is needed.
 */

/**
 * Shows [property] in this checkbox and writes every change back to it.
 *
 * @param property The property to bind, such as `settings::fullscreen`.
 * @return This checkbox, for chaining.
 */
fun FlixelUiCheckbox.bind(property: KMutableProperty0<Boolean>): FlixelUiCheckbox {
  setChecked(property.get(), false)
  onChange.add { property.set(it.isChecked) }
  return this
}

/**
 * Shows [property] in this text box and writes every edit back to it.
 *
 * Each edit copies the text into a new [String] for the property, so this suits short fields such
 * as a player name rather than a large notes box.
 *
 * @param property The property to bind, such as `settings::playerName`.
 * @return This text box, for chaining.
 */
fun FlixelTextBox.bind(property: KMutableProperty0<String>): FlixelTextBox {
  setText(property.get())
  onChange.add { property.set(it.text.toString()) }
  return this
}

/**
 * Selects the item at [property]'s index and writes every new selection back to it.
 *
 * Add the items before binding, so the stored index has an item to select.
 *
 * @param property The property holding the selected index, such as `settings::quality`.
 * @return This dropdown, for chaining.
 */
fun FlixelUiDropdown.bind(property: KMutableProperty0<Int>): FlixelUiDropdown {
  selectedIndex = property.get()
  onSelect.add { property.set(it.selectedIndex) }
  return this
}

/**
 * Selects the radio button at [property]'s index and writes every new selection back to it.
 *
 * Add the radio buttons to the group before binding. An index outside the group leaves the current
 * selection alone.
 *
 * @param property The property holding the selected index, such as `settings::difficulty`.
 * @return This group, for chaining.
 */
fun FlixelUiRadioGroup.bind(property: KMutableProperty0<Int>): FlixelUiRadioGroup {
  val index = property.get()
  if (index in 0 until count) {
    select(getRadioAt(index), false)
  }
  onChange.add { property.set(it.selectedIndex) }
  return this
}
