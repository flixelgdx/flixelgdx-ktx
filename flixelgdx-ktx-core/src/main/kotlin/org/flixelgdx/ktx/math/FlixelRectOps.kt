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
@file:JvmName("FlixelRectOps")

package org.flixelgdx.ktx.math

import org.flixelgdx.math.FlixelRect

/**
 * Arithmetic operators for [FlixelRect], placed in a separate file to avoid conflicts with the
 * destructuring helpers already defined in `FlixelRects.kt`.
 *
 * As with the vector operators in `FlixelVectors.kt`, there are two families:
 * - Compound-assignment operators (`+=`, `-=`) translate the rectangle's position in place and
 *   allocate nothing. Prefer these in per-frame code.
 * - Binary operators (`+`, `-`) each return a new [FlixelRect]. They read cleanly but allocate, so
 *   avoid them in update loops.
 */

/**
 * Translates this rectangle in place by moving its top-left corner by ([dx], [dy]).
 *
 * The width and height are unchanged. Allocates nothing.
 *
 * @param dx Horizontal delta.
 * @param dy Vertical delta.
 */
operator fun FlixelRect.plusAssign(other: Pair<Float, Float>) {
  x += other.first
  y += other.second
}

/**
 * Translates this rectangle in place by moving its top-left corner by the negation of ([dx], [dy]).
 *
 * Allocates nothing.
 *
 * @param other A pair of (dx, dy) deltas to subtract.
 */
operator fun FlixelRect.minusAssign(other: Pair<Float, Float>) {
  x -= other.first
  y -= other.second
}

/**
 * Returns a new rectangle whose position is this one shifted by ([other].first, [other].second).
 *
 * This allocates a new [FlixelRect]; use [plusAssign] in per-frame code.
 *
 * @param other A pair of (dx, dy) deltas.
 * @return A new translated rectangle.
 */
operator fun FlixelRect.plus(other: Pair<Float, Float>): FlixelRect =
  FlixelRect(x + other.first, y + other.second, width, height)

/**
 * Returns a new rectangle whose position is this one shifted by the negation of ([other].first,
 * [other].second).
 *
 * This allocates a new [FlixelRect]; use [minusAssign] in per-frame code.
 *
 * @param other A pair of (dx, dy) deltas.
 * @return A new translated rectangle.
 */
operator fun FlixelRect.minus(other: Pair<Float, Float>): FlixelRect =
  FlixelRect(x - other.first, y - other.second, width, height)

/**
 * Returns a new rectangle scaled by [factor] - both size and position are multiplied.
 *
 * This allocates a new [FlixelRect].
 *
 * @param factor The scalar to apply to x, y, width, and height.
 * @return A new scaled rectangle.
 */
operator fun FlixelRect.times(factor: Float): FlixelRect =
  FlixelRect(x * factor, y * factor, width * factor, height * factor)
