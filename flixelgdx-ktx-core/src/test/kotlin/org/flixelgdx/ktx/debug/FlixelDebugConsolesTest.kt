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
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.flixelgdx.debug.FlixelDebugCommandArgs
import org.flixelgdx.debug.FlixelDebugManager

class FlixelDebugConsolesTest {

  private val console = FlixelDebugManager().console

  @Test
  fun commandRegistersWithHelpText() {
    var seen: FlixelDebugCommandArgs? = null

    console.command("spawn", usage = "spawn <count>", description = "Spawns enemies.") { seen = it }
    console.execute("spawn 3")

    assertEquals(3, seen?.int(0))
    val index = (0 until console.commandCount).first { console.getCommandName(it) == "spawn" }
    assertEquals("spawn <count>", console.getCommandUsage(index))
    assertEquals("Spawns enemies.", console.getCommandDescription(index))
  }

  @Test
  fun containsAndMinusAssignMatchHasAndUnregister() {
    console.command("god") {}
    assertTrue("god" in console)

    console -= "god"

    assertFalse("god" in console)
  }

  @Test
  fun argsReadWithDefaults() {
    val args = FlixelDebugCommandArgs(arrayOf("12", "2.5", "on", "word"))

    assertEquals(4, args.size)
    assertEquals("word", args[3])
    assertNull(args[4])
    assertEquals(12, args.int(0))
    assertEquals(12L, args.long(0))
    assertEquals(2.5f, args.float(1))
    assertEquals(2.5, args.double(1))
    assertTrue(args.boolean(2))
    assertEquals(7, args.int(3, default = 7))
    assertEquals(0, args.int(9))
  }
}
