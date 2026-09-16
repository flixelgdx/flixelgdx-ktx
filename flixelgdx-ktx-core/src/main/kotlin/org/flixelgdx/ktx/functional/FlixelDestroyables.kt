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
@file:JvmName("FlixelDestroyables")

package org.flixelgdx.ktx.functional

import org.flixelgdx.functional.FlixelDestroyable

/**
 * Kotlin scope helpers for [FlixelDestroyable].
 *
 * [FlixelDestroyable] plays the same role as Java's [AutoCloseable]: it marks objects that hold
 * resources and need explicit cleanup. Unlike [AutoCloseable], it does not integrate with
 * try-with-resources by default. The [use] extension below fills that gap.
 */

/**
 * Executes [block] and guarantees [FlixelDestroyable.destroy] is called afterward, even if the
 * block throws.
 *
 * This mirrors Kotlin's standard [AutoCloseable.use] but works with any [FlixelDestroyable], so you
 * can manage engine objects with the same familiar idiom without needing them to implement
 * [AutoCloseable].
 *
 * Example:
 * ```
 * val timer = FlixelTimer()
 * timer.use {
 *   timer.start(3f) { nextState() }
 * }
 * // destroy() was called; the timer is cleaned up even if nextState() threw.
 * ```
 *
 * @param block The work to execute. The receiver is not passed as a parameter; reference it by the
 *   enclosing variable name instead.
 * @receiver The object whose [FlixelDestroyable.destroy] will be called in the finally block.
 */
inline fun FlixelDestroyable.use(block: () -> Unit) {
  try {
    block()
  } finally {
    destroy()
  }
}
