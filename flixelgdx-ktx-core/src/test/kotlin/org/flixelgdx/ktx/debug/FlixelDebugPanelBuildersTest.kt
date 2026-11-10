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
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertSame
import kotlin.test.assertTrue
import org.flixelgdx.debug.remote.FlixelDebugProtocol
import org.flixelgdx.debug.ui.FlixelDebugButton
import org.flixelgdx.debug.ui.FlixelDebugCheckbox
import org.flixelgdx.debug.ui.FlixelDebugExpander
import org.flixelgdx.debug.ui.FlixelDebugLabel
import org.flixelgdx.debug.ui.FlixelDebugPanel
import org.flixelgdx.debug.ui.FlixelDebugPanels
import org.flixelgdx.debug.ui.FlixelDebugProgress
import org.flixelgdx.debug.ui.FlixelDebugSlider
import org.flixelgdx.debug.ui.FlixelDebugStack
import org.flixelgdx.debug.ui.FlixelDebugTable
import org.flixelgdx.debug.ui.FlixelDebugTabs

class FlixelDebugPanelBuildersTest {

  private val panels = FlixelDebugPanels(null)

  private var frozen = false
  private var speed = 1.0
  private var kills = 0
  private var frames = 0

  @Test
  fun panelBuildsTheWidgetTreeAndRegistersIt() {
    val panel =
      panels.panel("game.enemies", "Enemies", dock = FlixelDebugDock.RIGHT) {
        heading("Enemies")
        checkbox("Frozen", { frozen }) { frozen = it }
        slider(0.0..2.0, { speed }) { speed = it }
        row {
          button("Kill all", confirm = "Sure?", tone = FlixelDebugTone.ERROR) { kills++ }
          separator()
        }
        expander("More", expanded = true) { hint("Hidden away") }
        tabs { page("Stats") { label("FPS") } }
        table { column("Name") }
      }

    assertSame(panel, panels.get("game.enemies"))
    assertEquals(FlixelDebugProtocol.DOCK_RIGHT, panel.dock)
    val root = assertIs<FlixelDebugStack>(panel.content)
    assertEquals(7, root.childCount)
    assertIs<FlixelDebugLabel>(root.getChild(0))
    val slider = assertIs<FlixelDebugSlider>(root.getChild(2))
    assertEquals(0.0, slider.min)
    assertEquals(2.0, slider.max)
    val row = assertIs<FlixelDebugStack>(root.getChild(3))
    assertTrue(row.isHorizontal)
    val button = assertIs<FlixelDebugButton>(row.getChild(0))
    assertEquals("Sure?", button.confirm)
    assertEquals(FlixelDebugTone.ERROR, button.tone)
    assertTrue(assertIs<FlixelDebugExpander>(root.getChild(4)).isExpanded)
    assertEquals(1, assertIs<FlixelDebugTabs>(root.getChild(5)).childCount)
    assertIs<FlixelDebugTable>(root.getChild(6))
    assertSame(panel, button.panel, "children added inside blocks belong to the panel")
  }

  @Test
  fun gettersRefreshAndHandlersRun() {
    lateinit var box: FlixelDebugCheckbox
    lateinit var button: FlixelDebugButton
    val panel =
      panels.panel("game.test", "Test") {
        box = checkbox("Frozen", { frozen }) { frozen = it }
        button = button("Kill") { kills++ }
      }

    frozen = true
    panel.update(0.016f)
    button.click()

    assertTrue(box.value)
    assertEquals(1, kills)
  }

  @Test
  fun onUpdateRunsEveryFrameAfterTheWidgetsRefresh() {
    lateinit var bar: FlixelDebugProgress
    val panel =
      panels.panel("game.test", "Test") {
        bar = progress()
        column { onUpdate { frames++ } }
      }

    panel.update(0.016f)
    panel.update(0.016f)

    assertEquals(2, frames)
    assertEquals(0.0, bar.value)
  }

  @Test
  fun replaceSwapsARegisteredPanel() {
    panels.panel("stats", "Stats") { label("old") }

    val replacement = panels.panel("stats", "Stats", replace = true) { label("new") }

    assertSame(replacement, panels.get("stats"))
    assertEquals(1, panels.count)
  }

  @Test
  fun onUpdateNeedsADslPanel() {
    val plain = FlixelDebugPanel("game.plain", "Plain")

    assertFailsWith<IllegalStateException> { plain.content { onUpdate {} } }
  }
}
