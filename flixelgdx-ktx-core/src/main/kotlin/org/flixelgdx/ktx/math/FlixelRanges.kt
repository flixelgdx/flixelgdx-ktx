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
@file:JvmName("FlixelRanges")

package org.flixelgdx.ktx.math

import org.flixelgdx.math.FlixelPointRangeBounds
import org.flixelgdx.math.FlixelRangeBounds
import org.flixelgdx.tween.ease.FlixelEaseFunction

/*
 * The range bounds types expose their curve as a public Java field of type FlixelEaseFunction.
 * Kotlin only converts lambdas and function references to a Java interface when they are passed
 * as function arguments, never when they are assigned to a field, so these setters give the field
 * a function-shaped entry point.
 */

/**
 * Sets the curve this range follows from its start value to its end value.
 *
 * This is the Kotlin-friendly way to assign [FlixelRangeBounds.ease], since a function reference or
 * lambda cannot be assigned to that field directly. The ease is stored once, so call this while
 * setting up (for example, in `create()`), not every frame.
 *
 * Example:
 * ```
 * emitter.alpha.set(1f, 1f, 0f, 0f)
 * emitter.alpha.ease(FlixelEase::quadIn)
 * emitter.speed.ease { t -> t * t }
 * ```
 *
 * @param ease The easing function to use, or `null` for a straight line.
 * @return This range, for chaining.
 */
fun FlixelRangeBounds.ease(ease: FlixelEaseFunction?): FlixelRangeBounds {
  this.ease = ease
  return this
}

/**
 * Sets the curve this point range follows from its start value to its end value.
 *
 * This is the Kotlin-friendly way to assign [FlixelPointRangeBounds.ease]. See the
 * [FlixelRangeBounds] overload for details.
 *
 * Example:
 * ```
 * emitter.scale.ease(FlixelEase::quadOut)
 * ```
 *
 * @param ease The easing function to use, or `null` for a straight line.
 * @return This range, for chaining.
 */
fun FlixelPointRangeBounds.ease(ease: FlixelEaseFunction?): FlixelPointRangeBounds {
  this.ease = ease
  return this
}
