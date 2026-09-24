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
import org.flixelgdx.tween.FlixelTween
import org.flixelgdx.tween.type.FlixelGoalTween
import org.flixelgdx.ui.FlixelUiLabel
import org.flixelgdx.util.FlixelAlign
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

// A running game registers the built-in tween types; tests have to do it themselves, exactly once.
private val goalTweenRegistration by lazy {
  FlixelTween.registerTweenType(FlixelGoalTween::class.java) { FlixelGoalTween(null) }
}

class FlixelUiTweensTest {

  @BeforeEach
  fun setUp() {
    headlessCamera()
    goalTweenRegistration
  }

  @Test
  fun `fadeIn goes from transparent to opaque`() {
    val label = FlixelUiLabel("Hi")
    val tween = label.fadeIn(duration = 1f)
    assertEquals(0f, label.alpha)
    tween.update(2f)
    assertEquals(1f, label.alpha, 1e-4f)
  }

  @Test
  fun `fadeOut goes to transparent`() {
    val label = FlixelUiLabel("Hi")
    label.fadeOut(duration = 1f).update(2f)
    assertEquals(0f, label.alpha, 1e-4f)
  }

  @Test
  fun `slideAnchor moves the anchor offsets and keeps the anchor`() {
    val label = FlixelUiLabel("Hi")
    label.anchor(FlixelAlign.RIGHT, 300f, 0f)
    label.slideAnchor(-16f, 8f, duration = 1f).update(2f)
    assertEquals(FlixelAlign.RIGHT, label.anchor)
    assertEquals(-16f, label.anchorOffsetX, 1e-4f)
    assertEquals(8f, label.anchorOffsetY, 1e-4f)
  }
}
