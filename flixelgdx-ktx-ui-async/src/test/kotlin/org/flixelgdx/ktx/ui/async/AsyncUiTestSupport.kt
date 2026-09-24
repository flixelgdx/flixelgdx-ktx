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

import org.flixelgdx.Flixel
import org.flixelgdx.FlixelCamera
import org.flixelgdx.backend.jvm.file.FlixelJvmFiles
import org.flixelgdx.ktx.ui.uiSkin
import org.flixelgdx.ui.FlixelUiDisplay

/** A headless display with plain "default" styles for the widgets the async tests build. */
fun testDisplay(): FlixelUiDisplay {
  if (Flixel.files !is FlixelJvmFiles) {
    Flixel.files = FlixelJvmFiles()
  }
  Flixel.cameras.clear()
  val camera = FlixelCamera(640, 360)
  Flixel.cameras.add(camera)
  val skin = uiSkin {
    button {}
    label {}
    modal {}
    checkbox {}
    radio {}
    dropdown {}
    textBox {}
  }
  return FlixelUiDisplay(camera, skin)
}
