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
@file:JvmName("FlixelTextFilters")

package org.flixelgdx.ktx.ui

import org.flixelgdx.ui.text.FlixelTextFilter

/**
 * Combinators for [FlixelTextFilter], so filters can be built from the ready-made ones.
 *
 * A lambda already works as a filter (`filter = FlixelTextFilter { it.isLetter() }`); these
 * operators combine existing filters instead of rewriting them:
 * ```kotlin
 * score.filter = FlixelTextFilter.DIGITS or acceptChars("-")
 * name.filter = FlixelTextFilter.ALPHANUMERIC and !acceptChars(" ")
 * ```
 *
 * Each combinator creates one small filter object when it is called, so build filters once (for
 * example while building the UI), not every frame.
 */

/**
 * Returns a filter that accepts a character only when both this filter and [other] accept it.
 *
 * @param other The second filter.
 * @return The combined filter.
 */
infix fun FlixelTextFilter.and(other: FlixelTextFilter): FlixelTextFilter = FlixelTextFilter {
  accept(it) && other.accept(it)
}

/**
 * Returns a filter that accepts a character when this filter or [other] accepts it.
 *
 * @param other The second filter.
 * @return The combined filter.
 */
infix fun FlixelTextFilter.or(other: FlixelTextFilter): FlixelTextFilter = FlixelTextFilter {
  accept(it) || other.accept(it)
}

/**
 * Returns a filter that accepts exactly the characters this filter rejects.
 *
 * @return The inverted filter.
 */
operator fun FlixelTextFilter.not(): FlixelTextFilter = FlixelTextFilter { !accept(it) }

/**
 * Returns a filter that accepts only the characters in [chars].
 *
 * @param chars Every character the filter accepts, such as `"-."`.
 * @return The new filter.
 */
fun acceptChars(chars: String): FlixelTextFilter = FlixelTextFilter { chars.indexOf(it) >= 0 }
