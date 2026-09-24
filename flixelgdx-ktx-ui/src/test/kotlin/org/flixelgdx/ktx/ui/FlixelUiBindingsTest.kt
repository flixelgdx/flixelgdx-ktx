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
import kotlin.test.assertTrue
import org.flixelgdx.ui.FlixelUiRadioGroup
import org.junit.jupiter.api.Test

class FlixelUiBindingsTest {

  private class Settings {
    var fullscreen = true
    var name = "Hero"
    var quality = 2
    var difficulty = 1
  }

  @Test
  fun `checkbox shows the property silently and writes changes back`() {
    val settings = Settings()
    val ui = testDisplay()
    var signals = 0
    ui.build {
      val box = checkbox("Fullscreen").bind(settings::fullscreen)
      assertTrue(box.isChecked)
      box.onChange.add { signals++ }
      box.click()
    }
    assertEquals(false, settings.fullscreen)
    assertEquals(1, signals)
  }

  @Test
  fun `text box writes every edit back`() {
    val settings = Settings()
    val ui = testDisplay()
    ui.build {
      val box = textBox(200f).bind(settings::name)
      assertEquals("Hero", box.text.toString())
      box.type('!')
    }
    assertEquals("Hero!", settings.name)
  }

  @Test
  fun `dropdown selects the stored index and writes selections back`() {
    val settings = Settings()
    val ui = testDisplay()
    ui.build {
      val quality = dropdown(160f) { items("Low", "Medium", "High") }.bind(settings::quality)
      assertEquals(2, quality.selectedIndex)
      quality.select(0)
    }
    assertEquals(0, settings.quality)
  }

  @Test
  fun `radio group selects the stored index and writes selections back`() {
    val settings = Settings()
    val group = FlixelUiRadioGroup()
    val ui = testDisplay()
    ui.build {
      radio("Easy", group)
      radio("Normal", group)
      radio("Hard", group)
    }
    group.bind(settings::difficulty)
    assertEquals(1, group.selectedIndex)
    group.select(2)
    assertEquals(2, settings.difficulty)
  }
}
