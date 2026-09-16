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
@file:JvmName("FlixelRandoms")

package org.flixelgdx.ktx.math

import org.flixelgdx.collections.FlixelArray
import org.flixelgdx.math.FlixelRandom

/**
 * Idiomatic Kotlin extensions for [FlixelRandom].
 *
 * These helpers let you pass Kotlin range literals directly to the random generator, so game code
 * reads like a natural description of the intent:
 * ```
 * val roll = rng.range(1..6)         // one integer from 1 to 6, inclusive
 * val scatter = rng.range(0f..0.5f)  // a float from 0 up to (not including) 0.5
 * val loot = rng.pick(lootTable)     // a random element from a FlixelArray
 * ```
 *
 * See [FlixelRandom] for the underlying RNG guarantees (seeded, deterministic, cross-platform).
 */

/**
 * Returns a random [Int] in [range], inclusive on both ends.
 *
 * This delegates to [FlixelRandom.nextInt] with [IntRange.first] and [IntRange.last], so a seed
 * produces the same result on every platform.
 *
 * @param range The closed integer range to draw from.
 * @return A value satisfying `value in range`.
 */
fun FlixelRandom.range(range: IntRange): Int = nextInt(range.first, range.last)

/**
 * Returns a random [Float] from [range].
 *
 * The lower bound is inclusive; the upper bound is exclusive, because the underlying
 * [FlixelRandom.nextFloat] generates values in `[min, max)`. In practice this distinction only
 * matters at the exact boundary and is rarely observable in game code.
 *
 * @param range The closed float range to draw from.
 * @return A value >= [ClosedRange.start] and < [ClosedRange.endInclusive].
 */
fun FlixelRandom.range(range: ClosedRange<Float>): Float =
  nextFloat(range.start, range.endInclusive)

/**
 * Returns a random element from [array].
 *
 * This is the [FlixelArray] counterpart to [FlixelRandom.pick] for plain arrays, with the same
 * seeded, cross-platform guarantee.
 *
 * @param array The array to pick from; must not be empty.
 * @param T The element type.
 * @return A randomly chosen element.
 * @throws IllegalArgumentException If [array] is empty.
 */
fun <T> FlixelRandom.pick(array: FlixelArray<T>): T {
  if (array.size == 0) throw IllegalArgumentException("cannot pick from an empty array")
  return array[nextInt(array.size)]
}
