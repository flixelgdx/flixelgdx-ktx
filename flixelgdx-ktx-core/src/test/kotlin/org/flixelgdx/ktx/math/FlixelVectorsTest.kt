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
import org.flixelgdx.math.FlixelVector
import org.junit.jupiter.api.Test

class FlixelVectorsTest {

  @Test
  fun `vector plus returns new vector with summed components`() {
    val v1 = FlixelVector(1f, 2f)
    val v2 = FlixelVector(3f, 4f)
    val result = v1 + v2
    assertEquals(4f, result.x, 0.0001f)
    assertEquals(6f, result.y, 0.0001f)
    // originals unchanged
    assertEquals(1f, v1.x, 0.0001f)
  }

  @Test
  fun `vector minus returns new vector with subtracted components`() {
    val v1 = FlixelVector(5f, 3f)
    val v2 = FlixelVector(2f, 1f)
    val result = v1 - v2
    assertEquals(3f, result.x, 0.0001f)
    assertEquals(2f, result.y, 0.0001f)
  }

  @Test
  fun `vector times returns scaled vector`() {
    val v = FlixelVector(2f, 4f)
    val result = v * 3f
    assertEquals(6f, result.x, 0.0001f)
    assertEquals(12f, result.y, 0.0001f)
  }

  @Test
  fun `vector plusAssign mutates in place`() {
    val v = FlixelVector(1f, 1f)
    v += FlixelVector(2f, 3f)
    assertEquals(3f, v.x, 0.0001f)
    assertEquals(4f, v.y, 0.0001f)
  }

  @Test
  fun `vector destructuring yields x and y`() {
    val v = FlixelVector(7f, 8f)
    val (x, y) = v
    assertEquals(7f, x, 0.0001f)
    assertEquals(8f, y, 0.0001f)
  }
}
