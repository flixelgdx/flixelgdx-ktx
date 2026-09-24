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
package org.flixelgdx.ktx.ui

import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue
import org.flixelgdx.ui.FlixelUiButton
import org.flixelgdx.ui.FlixelUiDisplay
import org.flixelgdx.ui.skin.FlixelUiButtonStyle
import org.flixelgdx.ui.skin.FlixelUiLabelStyle
import org.junit.jupiter.api.Test

class FlixelUiSkinsTest {

  @Test
  fun `uiSkin stores each declared style under its name`() {
    lateinit var danger: FlixelUiButtonStyle
    val skin = uiSkin {
      button { padLeft = 4f }
      danger = button("danger") { up = track(colorFill(0xAA2222FF)) }
      label("title") { fontSize = 32 }
    }

    assertEquals(4f, skin.get<FlixelUiButtonStyle>().padLeft)
    assertSame(danger, skin.get<FlixelUiButtonStyle>("danger"))
    assertEquals(32, skin.get<FlixelUiLabelStyle>("title").fontSize)
    assertTrue(skin.has<FlixelUiLabelStyle>("title"))
    assertFalse(skin.has<FlixelUiLabelStyle>())
  }

  @Test
  fun `set and remove work by type and name`() {
    val skin = uiSkin {}
    skin["fab"] = FlixelUiButtonStyle()
    assertTrue(skin.has<FlixelUiButtonStyle>("fab"))
    assertTrue(skin.remove<FlixelUiButtonStyle>("fab"))
    assertFalse(skin.has<FlixelUiButtonStyle>("fab"))
  }

  @Test
  fun `styles adds to an existing skin`() {
    val skin = testSkin()
    skin.styles { button("big") { padTop = 12f } }
    assertEquals(12f, skin.get<FlixelUiButtonStyle>("big").padTop)
  }

  @Test
  fun `widgets resolve styles declared with the DSL`() {
    val skin = uiSkin { button("wide") { padLeft = 20f } }
    val ui = FlixelUiDisplay(headlessCamera(), skin)
    lateinit var button: FlixelUiButton
    ui.build { button = button("Go", style = "wide") }
    assertSame(skin.get<FlixelUiButtonStyle>("wide"), button.style)
  }
}
