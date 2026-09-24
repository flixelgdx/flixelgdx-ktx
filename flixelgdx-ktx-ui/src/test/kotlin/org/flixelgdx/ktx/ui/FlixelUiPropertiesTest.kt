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
package org.flixelgdx.ktx.ui

import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import org.flixelgdx.ui.FlixelUiButton
import org.flixelgdx.ui.FlixelUiCheckbox
import org.flixelgdx.ui.FlixelUiDropdown
import org.flixelgdx.ui.FlixelUiLabel
import org.flixelgdx.ui.FlixelUiModal
import org.flixelgdx.ui.FlixelUiRadioButton
import org.flixelgdx.ui.FlixelUiRadioGroup
import org.flixelgdx.ui.FlixelUiStack
import org.flixelgdx.ui.skin.FlixelUiModalStyle
import org.flixelgdx.ui.text.FlixelTextBox
import org.flixelgdx.ui.text.FlixelTextFilter
import org.flixelgdx.util.FlixelAlign
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Checks that the UI extension's getters and setters pair up into Kotlin `var` properties.
 *
 * Most of the value here is at compile time: if a Java change turns one of these into a read-only
 * `val` (for example a getter and setter with different nullability), this file stops compiling.
 */
class FlixelUiPropertiesTest {

  @BeforeEach
  fun setUp() {
    headlessCamera()
  }

  @Test
  fun `text box settings are properties`() {
    val box = FlixelTextBox(200f)
    box.placeholder = "PIN"
    box.passwordChar = '*'
    box.filter = FlixelTextFilter.DIGITS
    box.maxLength = 6
    box.isMultiLine = true
    box.text = "1234"
    box.caret = 2

    assertEquals("PIN", box.placeholder.toString())
    assertEquals('*', box.passwordChar)
    assertSame(FlixelTextFilter.DIGITS, box.filter)
    assertEquals(6, box.maxLength)
    assertTrue(box.isMultiLine)
    assertEquals("1234", box.text.toString())
    assertEquals(2, box.caret)

    box.filter = null
    assertNull(box.filter)
  }

  @Test
  fun `widget layout and style settings are properties`() {
    val button = FlixelUiButton("Go")
    button.text = "Play"
    button.styleName = "fab"
    button.anchor = FlixelAlign.BOTTOM_RIGHT
    button.anchorOffsetX = -24f
    button.anchorOffsetY = -12f
    button.percentWidth = 0.5f
    button.percentHeight = 1f
    button.isAutoSize = false

    assertEquals("Play", button.text.toString())
    assertEquals("fab", button.styleName)
    assertEquals(FlixelAlign.BOTTOM_RIGHT, button.anchor)
    assertEquals(-24f, button.anchorOffsetX)
    assertEquals(-12f, button.anchorOffsetY)
    assertEquals(0.5f, button.percentWidth)
    assertEquals(1f, button.percentHeight)
    assertEquals(false, button.isAutoSize)
  }

  @Test
  fun `captions and selections are properties`() {
    val label = FlixelUiLabel("a")
    label.text = "b"
    label.isAutoSize = false
    assertEquals("b", label.text.toString())

    val check = FlixelUiCheckbox("Sound")
    check.text = "Music"
    assertEquals("Music", check.text.toString())

    val group = FlixelUiRadioGroup()
    val easy = FlixelUiRadioButton("Easy", group)
    FlixelUiRadioButton("Hard", group)
    easy.text = "Casual"
    group.selectedIndex = 1
    assertEquals("Casual", easy.text.toString())
    assertEquals(1, group.selectedIndex)

    val dropdown = FlixelUiDropdown(100f)
    dropdown.placeholder = "Pick one"
    assertEquals("Pick one", dropdown.placeholder.toString())

    val stack = FlixelUiStack(FlixelUiStack.Direction.VERTICAL)
    stack.isAutoSize = false
    assertEquals(false, stack.isAutoSize)
  }

  @Test
  fun `modal and display styles are properties`() {
    val modal = FlixelUiModal(100f, 100f)
    val style = FlixelUiModalStyle()
    modal.modalStyle = style
    assertSame(style, modal.modalStyle)

    val ui = testDisplay()
    ui.tooltipStyle = "big"
    assertEquals("big", ui.tooltipStyle)
  }
}
