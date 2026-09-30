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
package org.flixelgdx.ktx.tween

import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import org.flixelgdx.math.FlixelPointRangeBounds
import org.flixelgdx.math.FlixelRangeBounds
import org.flixelgdx.tween.ease.FlixelEase
import org.flixelgdx.util.FlixelColor
import org.flixelgdx.util.FlixelColorRangeBounds
import org.junit.jupiter.api.Test

class FlixelEasesTest {

  @Test
  fun `range ease accepts a function reference`() {
    val range = FlixelRangeBounds(1f, 1f, 0f, 0f)
    val result = range.ease(FlixelEase::quadOut)
    assertSame(range, result)
    assertEquals(FlixelEase.quadOut(0.5f), range.ease!!.compute(0.5f), 0.0001f)
  }

  @Test
  fun `range ease accepts a lambda`() {
    val range = FlixelRangeBounds(0f)
    range.ease { t -> t * t }
    assertEquals(0.25f, range.ease!!.compute(0.5f), 0.0001f)
  }

  @Test
  fun `range ease accepts null to reset to linear`() {
    val range = FlixelRangeBounds(0f).ease(FlixelEase::quadIn)
    range.ease(null)
    assertNull(range.ease)
  }

  @Test
  fun `point range ease sets the field and chains`() {
    val range = FlixelPointRangeBounds(1f, 1f)
    val result = range.ease(FlixelEase::sineInOut)
    assertSame(range, result)
    assertEquals(FlixelEase.sineInOut(0.3f), range.ease!!.compute(0.3f), 0.0001f)
  }

  @Test
  fun `color range ease sets the field and chains`() {
    val range = FlixelColorRangeBounds(FlixelColor.WHITE)
    val result = range.ease { t -> 1f - t }
    assertSame(range, result)
    assertEquals(0.75f, range.ease!!.compute(0.25f), 0.0001f)
  }
}
