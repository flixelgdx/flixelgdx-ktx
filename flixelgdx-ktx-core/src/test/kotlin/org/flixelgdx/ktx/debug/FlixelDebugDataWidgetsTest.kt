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
import org.flixelgdx.debug.remote.FlixelDebugProtocol
import org.flixelgdx.debug.ui.FlixelDebugCanvas
import org.flixelgdx.debug.ui.FlixelDebugTable
import org.flixelgdx.debug.ui.FlixelDebugTree

class FlixelDebugDataWidgetsTest {

  @Test
  fun tableRowsAreReplacedAndUnchangedRowsAreDetected() {
    val table = FlixelDebugTable().column("Name").column("Size", 90f, alignRight = true)

    val changed =
      table.rows {
        row(1) {
          cell("a")
          cell(10)
        }
        row(2, FlixelDebugTone.ERROR) {
          cell("b")
          cell(20)
        }
      }
    val again =
      table.rows {
        row(1) {
          cell("a")
          cell(10)
        }
        row(2, FlixelDebugTone.ERROR) {
          cell("b")
          cell(20)
        }
      }

    assertTrue(changed)
    assertFalse(again, "the same rows should not count as a change")
    assertEquals(2, table.rowCount)
  }

  @Test
  fun treeNodesAreReplaced() {
    val tree = FlixelDebugTree()

    val changed =
      tree.nodes {
        node(1, 0, "Player") {
          tone(FlixelDebugTone.ACCENT).badge("agent", FlixelDebugTone.WARNING)
        }
        node(2, 1, "Weapon") { detail("Pistol") }
      }

    assertTrue(changed)
    assertEquals(2, tree.nodeCount)
  }

  @Test
  fun canvasShapesAndInteractFlags() {
    val canvas = FlixelDebugCanvas().setInteract(pick = true, zoom = true)

    canvas.shapes {
      shape(1, 0f, 0f, 10f, 10f, filled = true, label = "Box")
      shape(2, 5f, 5f, 2f, 2f, FlixelDebugTone.ERROR, selected = true)
    }

    assertEquals(2, canvas.shapeCount)
    assertEquals(
      FlixelDebugProtocol.INTERACT_PICK or FlixelDebugProtocol.INTERACT_ZOOM,
      canvas.interact,
    )
  }
}
