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
package org.flixelgdx.ktx.math

import kotlin.test.assertEquals
import org.flixelgdx.math.FlixelRect
import org.junit.jupiter.api.Test

class FlixelRectsTest {

  @Test
  fun `rect destructuring yields all four components`() {
    val r = FlixelRect(1f, 2f, 3f, 4f)
    val (x, y, w, h) = r
    assertEquals(1f, x, 0.0001f)
    assertEquals(2f, y, 0.0001f)
    assertEquals(3f, w, 0.0001f)
    assertEquals(4f, h, 0.0001f)
  }

  @Test
  fun `rect plus returns translated copy`() {
    val r = FlixelRect(1f, 2f, 10f, 5f)
    val shifted = r + (3f to 4f)
    assertEquals(4f, shifted.x, 0.0001f)
    assertEquals(6f, shifted.y, 0.0001f)
    assertEquals(10f, shifted.width, 0.0001f)
    // original unchanged
    assertEquals(1f, r.x, 0.0001f)
  }

  @Test
  fun `rect minus returns translated copy`() {
    val r = FlixelRect(5f, 6f, 10f, 5f)
    val shifted = r - (2f to 1f)
    assertEquals(3f, shifted.x, 0.0001f)
    assertEquals(5f, shifted.y, 0.0001f)
  }

  @Test
  fun `rect times scales all components`() {
    val r = FlixelRect(1f, 2f, 4f, 8f)
    val scaled = r * 2f
    assertEquals(2f, scaled.x, 0.0001f)
    assertEquals(4f, scaled.y, 0.0001f)
    assertEquals(8f, scaled.width, 0.0001f)
    assertEquals(16f, scaled.height, 0.0001f)
  }

  @Test
  fun `rect plusAssign translates in place`() {
    val r = FlixelRect(1f, 1f, 5f, 5f)
    r += (2f to 3f)
    assertEquals(3f, r.x, 0.0001f)
    assertEquals(4f, r.y, 0.0001f)
    assertEquals(5f, r.width, 0.0001f)
  }
}
