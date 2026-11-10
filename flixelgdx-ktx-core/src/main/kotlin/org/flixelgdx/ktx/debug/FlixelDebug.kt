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
@file:JvmName("FlixelDebug")

package org.flixelgdx.ktx.debug

import org.flixelgdx.Flixel
import org.flixelgdx.debug.FlixelDebugManager

/**
 * Runs [block] with [Flixel.debug] as its receiver, but only while the game runs in debug mode.
 *
 * Debugger calls are already safe to leave in a release build: they register a few small objects
 * and then cost nothing. Wrapping them in this block skips even that, and groups every debug
 * registration of a state in one place:
 * ```kotlin
 * override fun create() {
 *   debugOnly {
 *     watches.addFloat("Player X") { player.x }
 *     console.register("god") { player.invincible = !player.invincible }
 *   }
 * }
 * ```
 *
 * The function is `inline`, so the block is not allocated as an object.
 *
 * @param block The debug setup to run, with the debug manager as its receiver.
 * @see Flixel.isDebugMode
 */
inline fun debugOnly(block: FlixelDebugManager.() -> Unit) {
  if (Flixel.isDebugMode()) {
    Flixel.debug.block()
  }
}
