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
@file:JvmName("FlixelColorOps")

package org.flixelgdx.ktx.util

import org.flixelgdx.util.FlixelColor

/**
 * Additional operators and builders for [FlixelColor], kept in a separate file from the
 * destructuring helpers in `FlixelColors.kt` to avoid modifying that file.
 *
 * These cover the idioms Kotlin users reach for: an `lerp` infix operator for readable
 * interpolation expressions, plus [rgba] and [hsv] builder functions for constructing colors from
 * their natural coordinate systems. All of them allocate a new [FlixelColor], so build colors
 * during setup rather than every frame.
 *
 * Together with the packed-integer and hex-string factories in `FlixelColors.kt`
 * ([Int.toFlixelColor] and [String.toFlixelColor]), these builders cover every input form
 * [FlixelColor] itself accepts.
 */

/**
 * Returns a new color interpolated from this color toward [target] by `t`.
 *
 * `t = 0f` returns a copy of this color; `t = 1f` returns a copy of [target]. Values outside `[0,
 * 1]` are not clamped, matching [FlixelColor.lerp]'s own behavior.
 *
 * This allocates a new [FlixelColor]. To interpolate in place without allocating, call
 * [FlixelColor.lerp] directly.
 *
 * ```
 * val mid = FlixelColor.RED lerp FlixelColor.BLUE at 0.5f
 * // or:
 * val blended = start lerp end at progress
 * ```
 *
 * @param target The color to interpolate toward.
 * @return A new color at the midpoint controlled by `t`.
 */
infix fun FlixelColor.lerp(target: FlixelColor): LerpColorPartial = LerpColorPartial(this, target)

/**
 * Intermediate value returned by [lerp] so the expression can chain with `at`.
 *
 * This object is tiny and short-lived; in practice the JIT eliminates it in hot code.
 *
 * @property from The source color.
 * @property to The destination color.
 */
class LerpColorPartial(val from: FlixelColor, val to: FlixelColor) {

  /**
   * Completes the lerp by specifying the interpolation factor [t].
   *
   * @param t Interpolation factor in `[0, 1]`.
   * @return A new [FlixelColor] between [from] and [to].
   */
  infix fun at(t: Float): FlixelColor = FlixelColor(from).lerp(to, t)
}

/**
 * Creates a new [FlixelColor] from normalized RGBA components in `[0, 1]`.
 *
 * This allocates a new [FlixelColor]; build colors at setup time.
 *
 * ```
 * val coral = rgba(1f, 0.5f, 0.31f)
 * val semiTransparent = rgba(0f, 0f, 1f, a = 0.5f)
 * ```
 *
 * @param r Red component in `[0, 1]`.
 * @param g Green component in `[0, 1]`.
 * @param b Blue component in `[0, 1]`.
 * @param a Alpha component in `[0, 1]`; defaults to fully opaque.
 * @return A new color with the given components.
 */
fun rgba(r: Float, g: Float, b: Float, a: Float = 1f): FlixelColor = FlixelColor(r, g, b, a)

/**
 * Creates a new [FlixelColor] from 8-bit RGB components in `[0, 255]` and a normalized alpha in
 * `[0, 1]`, mirroring [FlixelColor]'s own `(int, int, int, float)` constructor.
 *
 * Components are clamped to their valid ranges, so out-of-range values do not throw. This allocates
 * a new [FlixelColor]; build colors at setup time.
 *
 * ```
 * val coral = rgba(255, 127, 80)
 * val semiTransparent = rgba(0, 0, 255, a = 0.5f)
 * ```
 *
 * @param r Red component in `[0, 255]`.
 * @param g Green component in `[0, 255]`.
 * @param b Blue component in `[0, 255]`.
 * @param a Alpha component in `[0, 1]`; defaults to fully opaque.
 * @return A new color with the given components.
 */
fun rgba(r: Int, g: Int, b: Int, a: Float = 1f): FlixelColor = FlixelColor(r, g, b, a)

/**
 * Creates a new [FlixelColor] from HSV (hue-saturation-value) components.
 *
 * Hue is specified in degrees, while saturation, value, and alpha are in `[0, 1]`. This is a pure
 * math conversion; it allocates exactly one [FlixelColor].
 *
 * ```
 * val crimson = hsv(345f, 0.9f, 0.8f)
 * val dimRed = hsv(0f, 1f, 0.5f, a = 0.8f)
 * ```
 *
 * @param hue Hue in degrees `[0, 360)`.
 * @param saturation Saturation in `[0, 1]`.
 * @param value Value (brightness) in `[0, 1]`.
 * @param a Alpha in `[0, 1]`; defaults to fully opaque.
 * @return A new color with the given HSV values.
 */
fun hsv(hue: Float, saturation: Float, value: Float, a: Float = 1f): FlixelColor {
  val color = FlixelColor(0f, 0f, 0f, a)
  color.setValue(value)
  color.setSaturation(saturation)
  color.setHue(hue)
  return color
}
