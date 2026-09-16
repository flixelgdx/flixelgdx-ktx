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
package org.flixelgdx.ktx.collections

import kotlin.test.assertEquals
import kotlin.test.assertFalse
import org.flixelgdx.collections.FlixelMap
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class FlixelMapsTest {

  @Test
  fun `flixelMapOf stores key-value pairs`() {
    val map = flixelMapOf("a" to 1, "b" to 2)
    assertEquals(1, map["a"])
    assertEquals(2, map["b"])
  }

  @Test
  fun `map contains operator works`() {
    val map = flixelMapOf("hello" to true)
    assertTrue("hello" in map)
    assertFalse("world" in map)
  }

  @Test
  fun `map set operator stores value`() {
    val map = FlixelMap<String, Int>()
    map["key"] = 42
    assertEquals(42, map["key"])
  }

  @Test
  fun `map isNotEmpty returns correct values`() {
    val empty = FlixelMap<String, Int>()
    assertFalse(empty.isNotEmpty())
    empty.put("k", 1)
    assertTrue(empty.isNotEmpty())
  }
}
