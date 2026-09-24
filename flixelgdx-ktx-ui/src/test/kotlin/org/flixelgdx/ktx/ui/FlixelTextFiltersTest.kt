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

import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.flixelgdx.ui.text.FlixelTextFilter
import org.junit.jupiter.api.Test

class FlixelTextFiltersTest {

  @Test
  fun `or accepts characters either filter accepts`() {
    val filter = FlixelTextFilter.DIGITS or acceptChars("-")
    assertTrue(filter.accept('7'))
    assertTrue(filter.accept('-'))
    assertFalse(filter.accept('a'))
  }

  @Test
  fun `and accepts characters both filters accept`() {
    val filter = FlixelTextFilter.ALPHANUMERIC and !acceptChars(" ")
    assertTrue(filter.accept('a'))
    assertFalse(filter.accept(' '))
    assertFalse(filter.accept('!'))
  }

  @Test
  fun `not inverts a filter`() {
    val filter = !FlixelTextFilter.DIGITS
    assertTrue(filter.accept('x'))
    assertFalse(filter.accept('3'))
  }
}
