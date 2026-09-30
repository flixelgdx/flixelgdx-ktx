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
import kotlin.test.assertSame
import kotlin.test.assertTrue
import org.flixelgdx.graphics.FlixelFrame
import org.flixelgdx.graphics.FlixelImage
import org.flixelgdx.graphics.FlixelTexture
import org.flixelgdx.ui.FlixelUiButton
import org.flixelgdx.ui.FlixelUiLabel
import org.flixelgdx.ui.FlixelUiPicture
import org.flixelgdx.ui.FlixelUiRadioGroup
import org.flixelgdx.ui.FlixelUiSlider
import org.flixelgdx.ui.FlixelUiStack
import org.flixelgdx.ui.skin.FlixelUiButtonStyle
import org.flixelgdx.util.FlixelAlign
import org.junit.jupiter.api.Test

class FlixelUiBuildersTest {

  @Test
  fun `uiDisplay builds the tree in source order`() {
    lateinit var play: FlixelUiButton
    lateinit var column: FlixelUiStack
    val ui =
      uiDisplay(headlessCamera(), testSkin()) {
        column =
          vstack(spacing = 8f) {
            label("Title")
            play = button("Play")
            button("Quit")
          }
      }

    assertEquals(1, ui.root.childCount)
    assertSame(column, ui.root.getChildAt(0))
    assertEquals(3, column.childCount)
    assertTrue(column.getChildAt(0) is FlixelUiLabel)
    assertSame(play, column.getChildAt(1))
    assertEquals(8f, column.spacing)
    assertSame(ui, play.display, "children are attached while the tree is built")
  }

  @Test
  fun `builders apply style, tooltip, and the block`() {
    val ui = testDisplay()
    ui.skin.add("danger", FlixelUiButtonStyle())
    var clicks = 0
    lateinit var quit: FlixelUiButton
    ui.build {
      quit =
        button("Quit", style = "danger", tooltip = "Leave the game") {
          anchor(FlixelAlign.BOTTOM_RIGHT)
          onClick { clicks++ }
        }
    }

    assertEquals("danger", quit.styleName)
    assertEquals("Leave the game", quit.tooltip.toString())
    assertEquals(FlixelAlign.BOTTOM_RIGHT, quit.anchor)
    quit.click()
    assertEquals(1, clicks)
  }

  @Test
  fun `stack align is only changed when given`() {
    val ui = testDisplay()
    lateinit var plain: FlixelUiStack
    lateinit var centered: FlixelUiStack
    ui.build {
      plain = vstack()
      centered = hstack(align = FlixelAlign.CENTER)
    }
    assertEquals(FlixelAlign.LEFT, plain.align)
    assertEquals(FlixelAlign.CENTER, centered.align)
  }

  @Test
  fun `radio buttons join the group they are given`() {
    val ui = testDisplay()
    val group = FlixelUiRadioGroup()
    ui.build {
      hstack {
        radio("Easy", group)
        radio("Hard", group)
      }
    }
    assertEquals(2, group.count)
    group.select(1)
    assertEquals(1, group.selectedIndex)
  }

  @Test
  fun `checkbox, dropdown, and text box builders pass their arguments`() {
    val ui = testDisplay()
    ui.build {
      val box = checkbox("Fullscreen", checked = true)
      assertTrue(box.isChecked)
      val quality = dropdown(160f) { items("Low", "High") }
      assertEquals(2, quality.itemCount)
      val name = textBox(200f, 40f)
      assertEquals(40f, name.height)
    }
  }

  @Test
  fun `modal builds its content before opening`() {
    val ui = testDisplay()
    var sawContent = -1
    val dialog =
      ui.modal(300f, 200f) {
        label("Are you sure?")
        onOpen.add { m -> sawContent = m.childCount }
      }
    assertTrue(dialog.isOpen)
    assertEquals(1, sawContent)
    assertTrue(dialog.destroyOnClose)
  }

  @Test
  fun `slider builders pass range, step, and orientation`() {
    val ui = testDisplay()
    var changes = 0
    lateinit var horizontal: FlixelUiSlider
    lateinit var vertical: FlixelUiSlider
    ui.build {
      horizontal =
        slider(200f, min = 0f, max = 100f, value = 50f, step = 10f, tooltip = "Volume") {
          onChange { changes++ }
        }
      vertical = vslider(80f, max = 10f, value = 2f)
    }

    assertEquals(50f, horizontal.value)
    assertEquals(100f, horizontal.max)
    assertEquals(10f, horizontal.step)
    assertEquals(false, horizontal.isVertical)
    assertEquals("Volume", horizontal.tooltip.toString())
    assertTrue(vertical.isVertical)
    assertEquals(2f, vertical.value)
    horizontal.setValue(70f)
    assertEquals(1, changes)
    assertSame(horizontal, ui.root.getChildAt(0))
  }

  @Test
  fun `picture builder shows the frame with the given size and scale mode`() {
    val ui = testDisplay()
    val frame = FlixelFrame(StubTexture(64, 32))
    lateinit var natural: FlixelUiPicture
    lateinit var filled: FlixelUiPicture
    ui.build {
      natural = picture(frame)
      filled =
        picture(frame, 100f, 100f, FlixelUiPicture.ScaleMode.FILL, tooltip = "Logo") {
          setRegion(0, 0, 16, 16)
        }
    }

    assertEquals(64, natural.naturalWidth)
    assertEquals(FlixelUiPicture.ScaleMode.FIT, natural.scaleMode)
    assertEquals(FlixelUiPicture.ScaleMode.FILL, filled.scaleMode)
    assertEquals(false, filled.isAutoSize)
    assertTrue(filled.hasRegion())
    assertEquals("Logo", filled.tooltip.toString())
    assertEquals(2, ui.root.childCount)
  }

  private class StubTexture(private val w: Int, private val h: Int) : FlixelTexture {
    override fun getHandle() = 1L

    override fun getWidth() = w

    override fun getHeight() = h

    override fun isSmooth() = false

    override fun setSmooth(smooth: Boolean) {}

    override fun update(x: Int, y: Int, image: FlixelImage) {}

    override fun destroy() {}
  }
}
