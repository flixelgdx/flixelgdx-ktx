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
import org.flixelgdx.collections.FlixelArray
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class FlixelArraysTest {

  @Test
  fun `flixelArrayOf builds array with correct elements`() {
    val array = flixelArrayOf("a", "b", "c")
    assertEquals(3, array.size)
    assertEquals("a", array[0])
    assertEquals("b", array[1])
    assertEquals("c", array[2])
  }

  @Test
  fun `plusAssign appends to FlixelArray`() {
    val array = flixelArrayOf("a")
    array += "b"
    assertEquals(2, array.size)
    assertEquals("b", array[1])
  }

  @Test
  fun `minusAssign removes from FlixelArray`() {
    val array = flixelArrayOf("a", "b", "c")
    array -= "b"
    assertEquals(2, array.size)
    assertFalse("b" in array)
  }

  @Test
  fun `contains returns true when element present`() {
    val array = flixelArrayOf(1, 2, 3)
    assertTrue(2 in array)
    assertFalse(99 in array)
  }

  @Test
  fun `lastIndex and indices computed correctly`() {
    val array = flixelArrayOf("x", "y", "z")
    assertEquals(2, array.lastIndex)
    assertEquals(0 until 3, array.indices)
  }

  @Test
  fun `isNotEmpty returns false on empty array and true when populated`() {
    val empty = FlixelArray<String>()
    assertFalse(empty.isNotEmpty())
    val filled = flixelArrayOf("item")
    assertTrue(filled.isNotEmpty())
  }

  @Test
  fun `flixelIntArrayOf stores all values`() {
    val arr = flixelIntArrayOf(10, 20, 30)
    assertEquals(3, arr.size)
    assertEquals(10, arr[0])
    assertEquals(30, arr[2])
  }
}
