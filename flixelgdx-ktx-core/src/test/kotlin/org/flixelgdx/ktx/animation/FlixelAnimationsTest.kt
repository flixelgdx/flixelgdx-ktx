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
package org.flixelgdx.ktx.animation

import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class FlixelAnimationsTest {

  @Test
  fun `IntRange toList toIntArray produces correct int array`() {
    val range = 0..5
    val indices = range.toList().toIntArray()
    assertEquals(6, indices.size)
    for (i in 0..5) {
      assertEquals(i, indices[i])
    }
  }

  @Test
  fun `single frame range produces one-element array`() {
    val indices = (3..3).toList().toIntArray()
    assertEquals(1, indices.size)
    assertEquals(3, indices[0])
  }

  @Test
  fun `fps to frame duration conversion is reciprocal`() {
    val fps = 12f
    val duration = 1f / fps
    assertEquals(0.0833f, duration, 0.001f)
  }
}
