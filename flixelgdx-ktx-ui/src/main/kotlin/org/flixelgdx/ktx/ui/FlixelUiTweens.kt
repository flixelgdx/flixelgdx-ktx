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
@file:JvmName("FlixelUiTweens")

package org.flixelgdx.ktx.ui

import org.flixelgdx.ktx.tween.tween
import org.flixelgdx.tween.FlixelTween
import org.flixelgdx.tween.FlixelTweenCallback
import org.flixelgdx.tween.ease.FlixelEaseFunction
import org.flixelgdx.ui.FlixelUiWidget

/**
 * Ready-made tweens for common widget animations, built on the KTX `tween(...)` DSL.
 *
 * A widget is a normal tween target, so `widget.tween { goal(...) }` works for anything else. These
 * helpers cover the cases that are easy to get wrong. In particular, an anchored widget's position
 * is owned by the layout, so tweening its X or Y fights the layout every frame; move it with
 * [slideAnchor], which tweens the anchor offsets instead.
 */

/**
 * Fades this widget in from fully transparent to fully opaque.
 *
 * @param duration Seconds the fade takes.
 * @param ease The easing function, or `null` for linear.
 * @param onComplete Runs when the fade finishes, or `null` for nothing.
 * @return The running tween.
 */
fun FlixelUiWidget.fadeIn(
  duration: Float = DEFAULT_FADE_SECONDS,
  ease: FlixelEaseFunction? = null,
  onComplete: FlixelTweenCallback? = null,
): FlixelTween {
  alpha = 0f
  return fadeTo(1f, duration, ease, onComplete)
}

/**
 * Fades this widget out from its current alpha to fully transparent.
 *
 * The widget stays visible (and hit-testable) at zero alpha; hide or remove it in [onComplete] if
 * it should stop receiving interactions.
 *
 * @param duration Seconds the fade takes.
 * @param ease The easing function, or `null` for linear.
 * @param onComplete Runs when the fade finishes, or `null` for nothing.
 * @return The running tween.
 */
fun FlixelUiWidget.fadeOut(
  duration: Float = DEFAULT_FADE_SECONDS,
  ease: FlixelEaseFunction? = null,
  onComplete: FlixelTweenCallback? = null,
): FlixelTween = fadeTo(0f, duration, ease, onComplete)

/**
 * Tweens this widget's alpha from its current value to [alpha].
 *
 * @param alpha The target alpha, from `0` to `1`.
 * @param duration Seconds the fade takes.
 * @param ease The easing function, or `null` for linear.
 * @param onComplete Runs when the fade finishes, or `null` for nothing.
 * @return The running tween.
 */
fun FlixelUiWidget.fadeTo(
  alpha: Float,
  duration: Float = DEFAULT_FADE_SECONDS,
  ease: FlixelEaseFunction? = null,
  onComplete: FlixelTweenCallback? = null,
): FlixelTween {
  val widget = this
  return tween(duration, ease, onComplete = onComplete) {
    goal(widget::getAlpha, alpha, widget::setAlpha)
  }
}

/**
 * Slides an anchored widget by tweening its anchor offsets to ([offsetX], [offsetY]).
 *
 * The anchor itself stays the same, so the widget keeps following its parent's size while it moves.
 * For example, to slide a panel in from off-screen:
 * ```kotlin
 * panel.anchor(FlixelAlign.RIGHT, 300f, 0f) // Start 300 pixels past the right edge.
 * panel.slideAnchor(-16f, 0f, duration = 0.3f)
 * ```
 *
 * @param offsetX The target X offset in pixels.
 * @param offsetY The target Y offset in pixels.
 * @param duration Seconds the slide takes.
 * @param ease The easing function, or `null` for linear.
 * @param onComplete Runs when the slide finishes, or `null` for nothing.
 * @return The running tween.
 */
fun FlixelUiWidget.slideAnchor(
  offsetX: Float,
  offsetY: Float,
  duration: Float = DEFAULT_FADE_SECONDS,
  ease: FlixelEaseFunction? = null,
  onComplete: FlixelTweenCallback? = null,
): FlixelTween {
  val widget = this
  return tween(duration, ease, onComplete = onComplete) {
    goal(widget::getAnchorOffsetX, offsetX) {
      widget.anchor(widget.anchor, it, widget.anchorOffsetY)
    }
    goal(widget::getAnchorOffsetY, offsetY) {
      widget.anchor(widget.anchor, widget.anchorOffsetX, it)
    }
  }
}

/** The default length of the widget tweens, in seconds. */
private const val DEFAULT_FADE_SECONDS = 0.25f
