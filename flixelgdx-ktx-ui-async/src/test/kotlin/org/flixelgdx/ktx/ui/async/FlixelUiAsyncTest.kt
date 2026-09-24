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
package org.flixelgdx.ktx.ui.async

import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.launch
import org.flixelgdx.ktx.async.Dispatcher
import org.flixelgdx.ktx.async.scope
import org.flixelgdx.ktx.ui.build
import org.flixelgdx.ktx.ui.button
import org.flixelgdx.ktx.ui.checkbox
import org.flixelgdx.ktx.ui.dropdown
import org.flixelgdx.ktx.ui.hstack
import org.flixelgdx.ktx.ui.items
import org.flixelgdx.ktx.ui.label
import org.flixelgdx.ktx.ui.modal
import org.flixelgdx.ktx.ui.onClick
import org.flixelgdx.ktx.ui.textBox
import org.flixelgdx.ui.FlixelUiButton
import org.flixelgdx.ui.FlixelUiModal
import org.junit.jupiter.api.Test

class FlixelUiAsyncTest {

  @Test
  fun `awaitClick resumes after a click`() {
    val ui = testDisplay()
    lateinit var button: FlixelUiButton
    ui.build { button = button("Go") }
    var clicked = false
    scope.launch(Dispatcher) {
      button.awaitClick()
      clicked = true
    }
    Dispatcher.update()
    assertFalse(clicked)
    button.click()
    Dispatcher.update()
    assertTrue(clicked)
  }

  @Test
  fun `value awaits report the value chosen when the signal fired`() {
    val ui = testDisplay()
    var checked: Boolean? = null
    var selected: Int? = null
    var submitted: String? = null
    ui.build {
      val box = checkbox("Sound")
      val quality = dropdown(100f) { items("Low", "High") }
      val name = textBox(200f)
      scope.launch(Dispatcher) { checked = box.awaitChange() }
      scope.launch(Dispatcher) { selected = quality.awaitSelect() }
      scope.launch(Dispatcher) { submitted = name.awaitSubmit() }
      Dispatcher.update()

      box.click()
      quality.select(1)
      name.insert("Hero")
      name.submit()
      // Change the values again before the coroutines resume.
      box.setChecked(false, false)
      name.text = ""
    }
    Dispatcher.update()
    assertEquals(true, checked)
    assertEquals(1, selected)
    assertEquals("Hero", submitted)
  }

  @Test
  fun `canceling an await removes its handler`() {
    val ui = testDisplay()
    lateinit var button: FlixelUiButton
    ui.build { button = button("Go") }
    var clicked = false
    val job =
      scope.launch(Dispatcher) {
        button.awaitClick()
        clicked = true
      }
    Dispatcher.update()
    job.cancel()
    Dispatcher.update()
    button.click()
    Dispatcher.update()
    assertFalse(clicked)
  }

  @Test
  fun `awaitClose returns once the modal closes`() {
    val ui = testDisplay()
    val modal = ui.modal(200f, 100f) { label("Hi") }
    var closed = false
    scope.launch(Dispatcher) {
      modal.awaitClose()
      closed = true
    }
    Dispatcher.update()
    assertFalse(closed)
    modal.close()
    Dispatcher.update()
    assertTrue(closed)
  }

  @Test
  fun `dialog returns the value passed to finish`() {
    val ui = testDisplay()
    lateinit var yes: FlixelUiButton
    var result: Boolean? = null
    scope.launch(Dispatcher) {
      result =
        ui.dialog(300f, 160f, dismissed = false) { finish ->
          label("Really quit?")
          hstack {
            yes = button("Yes") { onClick { finish(true) } }
            button("No") { onClick { finish(false) } }
          }
        }
    }
    Dispatcher.update()
    assertTrue(ui.isModalOpen)
    assertNull(result)

    yes.click()
    assertFalse(ui.isModalOpen, "finish closes the modal right away")
    Dispatcher.update()
    assertEquals(true, result)
  }

  @Test
  fun `dialog returns dismissed when closed another way`() {
    val ui = testDisplay()
    var result: String? = null
    scope.launch(Dispatcher) {
      result = ui.dialog(300f, 160f, dismissed = "cancel") { label("Pick one") }
    }
    Dispatcher.update()
    ui.closeModal()
    Dispatcher.update()
    assertEquals("cancel", result)
  }

  @Test
  fun `canceling a dialog closes its modal`() {
    val ui = testDisplay()
    var modal: FlixelUiModal? = null
    val job = scope.launch(Dispatcher) { ui.dialog(300f, 160f, dismissed = Unit) { modal = this } }
    Dispatcher.update()
    assertTrue(modal!!.isOpen)
    job.cancel()
    Dispatcher.update()
    assertFalse(ui.isModalOpen)
  }
}
