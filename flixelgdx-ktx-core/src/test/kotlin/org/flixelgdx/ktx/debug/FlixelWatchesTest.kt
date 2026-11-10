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
package org.flixelgdx.ktx.debug

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.flixelgdx.debug.feeds.FlixelDebugWatches
import org.flixelgdx.debug.remote.FlixelDebugMessageReader
import org.flixelgdx.debug.remote.FlixelDebugMessageWriter
import org.flixelgdx.debug.remote.FlixelDebugProtocol
import org.flixelgdx.util.FlixelString

class FlixelWatchesTest {

  private val watches = FlixelDebugWatches()

  /** Writes the watch named [name] and reads its value back the way the app would. */
  private fun read(name: String): Any? {
    val index = (0 until watches.count).first { watches.getName(it) == name }
    val writer = FlixelDebugMessageWriter(64)
    writer.beginMessage(FlixelDebugProtocol.MSG_FEED_DATA)
    watches.writeValue(index, writer)
    val reader = FlixelDebugMessageReader(writer.finish())
    reader.readU8()
    return when (val kind = reader.readU8()) {
      FlixelDebugProtocol.VALUE_INT -> reader.readI32()
      FlixelDebugProtocol.VALUE_LONG -> reader.readI64()
      FlixelDebugProtocol.VALUE_FLOAT -> reader.readF32()
      FlixelDebugProtocol.VALUE_DOUBLE -> reader.readF64()
      FlixelDebugProtocol.VALUE_BOOL -> reader.readBool()
      FlixelDebugProtocol.VALUE_STRING -> reader.readString(FlixelString()).toString()
      else -> error("Unexpected value kind $kind")
    }
  }

  @Test
  fun typedWatchesReadTheirGetters() {
    var x = 1.5f
    watches {
      int("Int") { 3 }
      long("Long") { 4L }
      float("Float") { x }
      double("Double") { 2.5 }
      boolean("Bool") { true }
      value("Text") { "hello" }
    }

    assertEquals(3, read("Int"))
    assertEquals(4L, read("Long"))
    assertEquals(1.5f, read("Float"))
    x = 9f
    assertEquals(9f, read("Float"))
    assertEquals(2.5, read("Double"))
    assertEquals(true, read("Bool"))
    assertEquals("hello", read("Text"))
  }

  @Test
  fun containsAndMinusAssignMatchHasAndRemove() {
    watches.int("Score") { 0 }
    assertTrue("Score" in watches)

    watches -= "Score"

    assertFalse("Score" in watches)
  }
}
