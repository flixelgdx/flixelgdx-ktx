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
package org.flixelgdx.ktx.util

import kotlin.test.assertEquals
import org.flixelgdx.util.FlixelColor
import org.junit.jupiter.api.Test

class FlixelColorsTest {

  @Test
  fun `color destructuring yields r g b a`() {
    val c = FlixelColor(1f, 0.5f, 0.25f, 0.75f)
    val (r, g, b, a) = c
    assertEquals(1f, r, 0.0001f)
    assertEquals(0.5f, g, 0.0001f)
    assertEquals(0.25f, b, 0.0001f)
    assertEquals(0.75f, a, 0.0001f)
  }

  @Test
  fun `Int toFlixelColor round-trips`() {
    // 0xFF0000FF = fully opaque red
    val packed = 0xFF0000FF.toInt()
    val color = packed.toFlixelColor()
    assertEquals(1f, color.r, 0.01f)
    assertEquals(0f, color.g, 0.01f)
    assertEquals(0f, color.b, 0.01f)
    assertEquals(1f, color.a, 0.01f)
  }

  @Test
  fun `rgba builder produces correct components`() {
    val c = rgba(0.2f, 0.4f, 0.6f, 0.8f)
    assertEquals(0.2f, c.r, 0.0001f)
    assertEquals(0.4f, c.g, 0.0001f)
    assertEquals(0.6f, c.b, 0.0001f)
    assertEquals(0.8f, c.a, 0.0001f)
  }

  @Test
  fun `rgba builder defaults alpha to 1`() {
    val c = rgba(1f, 0f, 0f)
    assertEquals(1f, c.a, 0.0001f)
  }

  @Test
  fun `rgba int builder maps 0-255 components to 0-1`() {
    val c = rgba(255, 127, 0, 0.5f)
    assertEquals(1f, c.r, 0.0001f)
    assertEquals(127f / 255f, c.g, 0.0001f)
    assertEquals(0f, c.b, 0.0001f)
    assertEquals(0.5f, c.a, 0.0001f)
  }

  @Test
  fun `rgba int builder defaults alpha to 1`() {
    val c = rgba(10, 20, 30)
    assertEquals(1f, c.a, 0.0001f)
  }

  @Test
  fun `hsv builder round-trips hue`() {
    // Pure red in HSV is (0, 1, 1)
    val c = hsv(0f, 1f, 1f)
    assertEquals(1f, c.r, 0.05f)
    assertEquals(0f, c.g, 0.05f)
    assertEquals(0f, c.b, 0.05f)
  }

  @Test
  fun `lerp infix operator returns intermediate color`() {
    val red = FlixelColor(1f, 0f, 0f, 1f)
    val blue = FlixelColor(0f, 0f, 1f, 1f)
    val mid = red lerp blue at 0.5f
    assertEquals(0.5f, mid.r, 0.01f)
    assertEquals(0.5f, mid.b, 0.01f)
    // Originals must not be mutated
    assertEquals(1f, red.r, 0.0001f)
    assertEquals(1f, blue.b, 0.0001f)
  }

  @Test
  fun `lerp at 0 returns copy of source color`() {
    val src = FlixelColor(0.3f, 0.5f, 0.7f, 1f)
    val dst = FlixelColor(1f, 1f, 1f, 1f)
    val result = src lerp dst at 0f
    assertEquals(src.r, result.r, 0.0001f)
    assertEquals(src.g, result.g, 0.0001f)
    assertEquals(src.b, result.b, 0.0001f)
  }
}
