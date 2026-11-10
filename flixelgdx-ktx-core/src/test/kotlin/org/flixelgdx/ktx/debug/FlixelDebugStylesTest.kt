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
import kotlin.test.assertTrue
import org.flixelgdx.debug.remote.FlixelDebugProtocol
import org.flixelgdx.debug.ui.FlixelDebugLabel
import org.flixelgdx.debug.ui.FlixelDebugPanel
import org.flixelgdx.debug.ui.FlixelDebugPanels

class FlixelDebugStylesTest {

  @Test
  fun toneWrapsTheProtocolCodes() {
    assertEquals(FlixelDebugProtocol.TONE_NORMAL, FlixelDebugTone.NORMAL.code)
    assertEquals(FlixelDebugProtocol.TONE_ERROR, FlixelDebugTone.ERROR.code)
    assertEquals(FlixelDebugProtocol.DOCK_RIGHT, FlixelDebugDock.RIGHT.code)
  }

  @Test
  fun settingToneKeepsTheOtherStyleFlags() {
    val label = FlixelDebugLabel("Hi")
    label.setStyle(FlixelDebugProtocol.STYLE_BOLD or FlixelDebugProtocol.TONE_MUTED)

    label.tone = FlixelDebugTone.WARNING

    assertEquals(FlixelDebugTone.WARNING, label.tone)
    assertEquals(FlixelDebugProtocol.STYLE_BOLD or FlixelDebugProtocol.TONE_WARNING, label.style)
  }

  @Test
  fun setDockUsesTheDockCode() {
    val panel = FlixelDebugPanel("game.test", "Test").setDock(FlixelDebugDock.BOTTOM)

    assertEquals(FlixelDebugProtocol.DOCK_BOTTOM, panel.dock)
  }

  @Test
  fun toastQueuesARequest() {
    val panels = FlixelDebugPanels(null)

    panels.toast(FlixelDebugTone.SUCCESS, "Saved")

    assertTrue(panels.hasRequest())
  }
}
