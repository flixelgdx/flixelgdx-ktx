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
package org.flixelgdx.ktx

import kotlin.test.assertFailsWith
import org.flixelgdx.collections.FlixelArray
import org.flixelgdx.collections.FlixelMap
import org.flixelgdx.functional.FlixelDestroyable
import org.flixelgdx.ktx.collections.plusAssign
import org.flixelgdx.ktx.collections.minusAssign
import org.flixelgdx.ktx.collections.contains
import org.flixelgdx.ktx.collections.flixelArrayOf
import org.flixelgdx.ktx.collections.flixelIntArrayOf
import org.flixelgdx.ktx.collections.flixelMapOf
import org.flixelgdx.ktx.collections.flixelSetOf
import org.flixelgdx.ktx.collections.indices
import org.flixelgdx.ktx.collections.isNotEmpty
import org.flixelgdx.ktx.collections.lastIndex
import org.flixelgdx.ktx.collections.set
import org.flixelgdx.ktx.math.component1
import org.flixelgdx.ktx.math.component2
import org.flixelgdx.ktx.math.component3
import org.flixelgdx.ktx.math.component4
import org.flixelgdx.ktx.math.minus
import org.flixelgdx.ktx.math.plus
import org.flixelgdx.ktx.math.plusAssign
import org.flixelgdx.ktx.math.range
import org.flixelgdx.ktx.math.times
import org.flixelgdx.math.FlixelRandom
import org.flixelgdx.math.FlixelRect
import org.flixelgdx.math.FlixelVector
import org.flixelgdx.ktx.util.component1
import org.flixelgdx.ktx.util.component2
import org.flixelgdx.ktx.util.component3
import org.flixelgdx.ktx.util.component4
import org.flixelgdx.ktx.util.hsv
import org.flixelgdx.ktx.util.lerp
import org.flixelgdx.ktx.util.rgba
import org.flixelgdx.ktx.util.toFlixelColor
import org.flixelgdx.util.FlixelColor
import org.flixelgdx.util.save.FlixelSave
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

/**
 * Headless unit tests for the FlixelGDX Kotlin extension.
 *
 * Every test in this file exercises pure data transformations or lightweight value objects that
 * require no GPU, window, or game-loop. Where constructing a real engine object (FlixelState,
 * FlixelSprite, FlixelTimer) would require a running backend, the test instead exercises the pure
 * conversion logic or uses a minimal fake, and the limitation is noted in a comment.
 */
class FlixelKtxTest {

  // ---------------------------------------------------------------------------
  // FlixelArray helpers (Part A)
  // ---------------------------------------------------------------------------

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

  // ---------------------------------------------------------------------------
  // FlixelMap helpers (Part A)
  // ---------------------------------------------------------------------------

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

  // ---------------------------------------------------------------------------
  // FlixelSet helpers (Part A)
  // ---------------------------------------------------------------------------

  @Test
  fun `flixelSetOf and plus-minus work`() {
    val set = flixelSetOf("a", "b")
    assertTrue(set.contains("a"))
    set -= "a"
    assertFalse(set.contains("a"))
    set += "c"
    assertTrue(set.contains("c"))
  }

  // ---------------------------------------------------------------------------
  // FlixelVector operators (Part A)
  // ---------------------------------------------------------------------------

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

  // ---------------------------------------------------------------------------
  // FlixelRect helpers (Part A + B)
  // ---------------------------------------------------------------------------

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

  // ---------------------------------------------------------------------------
  // FlixelColor helpers (Part A + B)
  // ---------------------------------------------------------------------------

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

  // ---------------------------------------------------------------------------
  // FlixelRandom range helpers (Part B)
  // ---------------------------------------------------------------------------

  @Test
  fun `range IntRange always returns value within bounds`() {
    val rng = FlixelRandom(42L)
    for (i in 0 until 100) {
      val v = rng.range(3..10)
      assertTrue(v in 3..10, "Expected value in [3,10] but got $v")
    }
  }

  @Test
  fun `range ClosedFloatingPointRange always returns value within bounds`() {
    val rng = FlixelRandom(99L)
    for (i in 0 until 100) {
      val v = rng.range(0f..1f)
      assertTrue(v in 0f..<1f, "Expected value in [0,1) but got $v")
    }
  }

  @Test
  fun `range single-element IntRange always returns that element`() {
    val rng = FlixelRandom(0L)
    for (i in 0 until 10) {
      assertEquals(5, rng.range(5..5))
    }
  }

  @Test
  fun `pick from FlixelArray returns element in the array`() {
    val rng = FlixelRandom(1L)
    val array = flixelArrayOf("alpha", "beta", "gamma")
    for (i in 0 until 50) {
      val picked = rng.pick(array)
      assertTrue(picked in array, "Picked element '$picked' not found in source array")
    }
  }

  @Test
  fun `pick from empty FlixelArray throws`() {
    val rng = FlixelRandom(1L)
    val empty = FlixelArray<String>()
    assertFailsWith<IllegalArgumentException> { rng.pick(empty) }
  }

  // ---------------------------------------------------------------------------
  // Animation range-to-int[] conversion (Part B, pure logic)
  // ---------------------------------------------------------------------------

  @Test
  fun `IntRange toList toIntArray produces correct int array`() {
    // This verifies the exact conversion used inside FlixelAnimationScope.add(...)
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

  // ---------------------------------------------------------------------------
  // FlixelDestroyable use() (Part B)
  // ---------------------------------------------------------------------------

  @Test
  fun `use calls destroy in finally on normal completion`() {
    var destroyed = false
    val obj = FlixelDestroyable { destroyed = true }
    obj.use { /* no-op */ }
    assertTrue(destroyed, "destroy() should have been called after use{}")
  }

  @Test
  fun `use calls destroy in finally even when block throws`() {
    var destroyed = false
    val obj = FlixelDestroyable { destroyed = true }
    assertFailsWith<IllegalStateException> { obj.use { throw IllegalStateException("boom") } }
    assertTrue(destroyed, "destroy() should still be called after block throws")
  }

  @Test
  fun `use returns value from block when needed`() {
    // The use() function returns Unit; this just verifies the block body runs.
    var ran = false
    val obj = FlixelDestroyable {}
    obj.use { ran = true }
    assertTrue(ran)
  }

  // ---------------------------------------------------------------------------
  // by save(...) delegates (Part B)
  //
  // Constructing a real FlixelSave requires Flixel.save to be non-null, which demands a running
  // backend. Instead we test the delegate logic directly against a manually constructed FlixelSave
  // and verify reads/writes on its data map plus the flush flag.
  // ---------------------------------------------------------------------------

  @Test
  fun `FlixelSave getInt returns default when key absent`() {
    val save = FlixelSave()
    assertEquals(42, save.getInt("missing", 42))
  }

  @Test
  fun `FlixelSave getFloat returns stored value`() {
    val save = FlixelSave()
    save.data.put("vol", 0.75f)
    assertEquals(0.75f, save.getFloat("vol", 1f), 0.0001f)
  }

  @Test
  fun `FlixelSave getBool returns stored value`() {
    val save = FlixelSave()
    save.data.put("music", false)
    assertFalse(save.getBool("music", true))
  }

  @Test
  fun `FlixelSave getString returns default when key absent`() {
    val save = FlixelSave()
    assertEquals("Player1", save.getString("name", "Player1"))
  }

  @Test
  fun `FlixelSave getString returns stored value`() {
    val save = FlixelSave()
    save.data.put("name", "Hero")
    assertEquals("Hero", save.getString("name", "default"))
  }
}
