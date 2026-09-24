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
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue
import org.flixelgdx.ui.FlixelUiContainer
import org.flixelgdx.ui.FlixelUiDropdown
import org.flixelgdx.ui.FlixelUiLabel
import org.flixelgdx.ui.FlixelUiWidget
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FlixelUiContainersTest {

  @BeforeEach
  fun setUp() {
    headlessCamera()
  }

  @Test
  fun `operators add, index, find, and remove children`() {
    val container = FlixelUiContainer(100f, 100f)
    val a = FlixelUiLabel("a")
    val b = FlixelUiLabel("b")
    container += a
    container += b

    assertEquals(2, container.size)
    assertSame(b, container[1])
    assertTrue(a in container)

    container -= a
    assertFalse(a in container)
    assertEquals(1, container.size)
  }

  @Test
  fun `forEachChild visits children in draw order`() {
    val container = FlixelUiContainer()
    val a = FlixelUiLabel("a")
    val b = FlixelUiLabel("b")
    container += a
    container += b
    val seen = arrayOfNulls<FlixelUiWidget>(2)
    var i = 0
    container.forEachChild { seen[i++] = it }
    assertSame(a, seen[0])
    assertSame(b, seen[1])
  }

  @Test
  fun `padding helpers set each side`() {
    val container = FlixelUiContainer()
    container.padding(horizontal = 12f, vertical = 4f)
    assertEquals(12f, container.paddingLeft)
    assertEquals(4f, container.paddingTop)
    assertEquals(12f, container.paddingRight)
    assertEquals(4f, container.paddingBottom)
    assertTrue(container.padding.isNaN(), "sides differ")
    container.padding = 6f
    assertEquals(6f, container.paddingBottom)
    assertEquals(6f, container.padding)
  }

  @Test
  fun `dropdown operators work on item text`() {
    val dropdown = FlixelUiDropdown(100f)
    dropdown.items("Low", "Medium")
    dropdown += "High"
    assertEquals(3, dropdown.itemCount)
    assertEquals("High", dropdown[2].toString())
  }
}
