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
import org.flixelgdx.debug.inspector.FlixelDebugValue
import org.flixelgdx.debug.inspector.FlixelPropertyBuilder
import org.flixelgdx.debug.remote.FlixelDebugMessageReader
import org.flixelgdx.debug.remote.FlixelDebugMessageWriter
import org.flixelgdx.debug.remote.FlixelDebugProtocol
import org.flixelgdx.debug.ui.FlixelDebugProperties

class FlixelDebugPropertyGridsTest {

  enum class Mode {
    IDLE,
    CHASE,
  }

  private var count = 3
  private var big = 9L
  private var aggression = 0.5f
  private var time = 1.25
  private var paused = false
  private var label = "hi"
  private var mode = Mode.CHASE

  private fun FlixelPropertyBuilder.all() {
    int("Count", { count }) { count = it }
    long("Big", { big }) { big = it }
    float("Aggression", { aggression }) { aggression = it }
    double("Time", { time }) { time = it }
    boolean("Paused", { paused }) { paused = it }
    text("Label", { it.concat(label) }) { label = it.toString() }
    enum("Mode") { mode }
    int("Fixed", once = true) { 42 }
  }

  private data class Row(val name: String, val value: String, val editable: Boolean)

  /** Writes every row and reads them back the way the app would. */
  private fun rows(builder: FlixelPropertyBuilder): List<Row> {
    val writer = FlixelDebugMessageWriter(64)
    writer.beginMessage(FlixelDebugProtocol.MSG_FEED_DATA)
    val n = builder.writeItems(writer)
    val reader = FlixelDebugMessageReader(writer.finish())
    reader.readU8()
    return List(n) {
      reader.readU8()
      reader.readCount()
      val name = FlixelDebugValue().read(reader).text.toString()
      val value = FlixelDebugValue().read(reader)
      val shown = if (value.isNumber) value.asDouble().toString() else value.text.toString()
      Row(name, shown, FlixelDebugValue().read(reader).asBoolean())
    }
  }

  private fun edit(write: FlixelDebugMessageWriter.() -> Unit): FlixelDebugValue {
    val writer = FlixelDebugMessageWriter(64)
    writer.beginMessage(FlixelDebugProtocol.MSG_FEED_DATA)
    writer.write()
    val reader = FlixelDebugMessageReader(writer.finish())
    reader.readU8()
    return FlixelDebugValue().read(reader)
  }

  @Test
  fun rowsReadTheirGetters() {
    val builder = FlixelPropertyBuilder().apply { all() }

    val rows = rows(builder)

    assertEquals(8, builder.rowCount)
    assertEquals(Row("Count", "3.0", true), rows[0])
    assertEquals(Row("Aggression", "0.5", true), rows[2])
    assertEquals(Row("Label", "hi", true), rows[5])
    assertEquals(Row("Mode", "CHASE", false), rows[6])
    assertEquals(Row("Fixed", "42.0", false), rows[7])
  }

  @Test
  fun editsCallTheSetters() {
    val builder = FlixelPropertyBuilder().apply { all() }

    builder.apply(0, edit { writeValue(7) })
    builder.apply(1, edit { writeValue(11L) })
    builder.apply(2, edit { writeValue(0.75f) })
    builder.apply(3, edit { writeValue(2.0) })
    builder.apply(4, edit { writeValue(true) })
    builder.apply(5, edit { writeValue("yo") })

    assertEquals(7, count)
    assertEquals(11L, big)
    assertEquals(0.75f, aggression)
    assertEquals(2.0, time)
    assertEquals(true, paused)
    assertEquals("yo", label)
  }

  @Test
  fun groupAddsASectionToTheGrid() {
    val grid =
      FlixelDebugProperties().group("AI") { all() }.group("World") { double("Time") { time } }

    assertEquals(2, grid.sectionCount)
    assertFalse(paused)
  }
}
