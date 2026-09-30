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
@file:JvmName("FlixelColorRanges")

package org.flixelgdx.ktx.util

import org.flixelgdx.tween.ease.FlixelEaseFunction
import org.flixelgdx.util.FlixelColorRangeBounds

/*
 * The color range bounds type exposes its curve as a public Java field of type FlixelEaseFunction.
 * Kotlin only converts lambdas and function references to a Java interface when they are passed
 * as function arguments, never when they are assigned to a field, so this setter gives the field
 * a function-shaped entry point.
 */

/**
 * Sets the curve this color range follows from its start color to its end color.
 *
 * This is the Kotlin-friendly way to assign [FlixelColorRangeBounds.ease]. It works like the range
 * bounds overloads in `org.flixelgdx.ktx.math`: set it once during setup, not every frame.
 *
 * Example:
 * ```
 * emitter.color.ease(FlixelEase::sineInOut)
 * ```
 *
 * @param ease The easing function to use, or `null` for a straight line.
 * @return This range, for chaining.
 */
fun FlixelColorRangeBounds.ease(ease: FlixelEaseFunction?): FlixelColorRangeBounds {
  this.ease = ease
  return this
}
