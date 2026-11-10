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
@file:JvmName("FlixelWatches")

package org.flixelgdx.ktx.debug

import org.flixelgdx.debug.feeds.FlixelDebugWatches

/**
 * Runs [block] with these watches as its receiver, so several watches can be added without
 * repeating `Flixel.debug.watches`:
 * ```kotlin
 * Flixel.debug.watches {
 *   float("Player X") { player.x }
 *   int("Score") { score }
 *   value("State") { machine.current.name }
 * }
 * ```
 *
 * Each typed function (`int`, `float`, and so on) is `inline` and passes the value through the
 * matching primitive getter, so reading a watch never boxes the number. Adding a name that already
 * exists replaces the old watch.
 *
 * @param block Adds the watches.
 */
inline operator fun FlixelDebugWatches.invoke(block: FlixelDebugWatches.() -> Unit) = block()

/**
 * Watches an `Int` value under [name].
 *
 * @param name The label shown next to the value.
 * @param getter Reads the current value. It runs often, so keep it cheap and allocation free.
 */
inline fun FlixelDebugWatches.int(name: String, crossinline getter: () -> Int) {
  addInt(name) { getter() }
}

/**
 * Watches a `Long` value under [name].
 *
 * @param name The label shown next to the value.
 * @param getter Reads the current value. It runs often, so keep it cheap and allocation free.
 */
inline fun FlixelDebugWatches.long(name: String, crossinline getter: () -> Long) {
  addLong(name) { getter() }
}

/**
 * Watches a `Float` value under [name].
 *
 * @param name The label shown next to the value.
 * @param getter Reads the current value. It runs often, so keep it cheap and allocation free.
 */
inline fun FlixelDebugWatches.float(name: String, crossinline getter: () -> Float) {
  addFloat(name) { getter() }
}

/**
 * Watches a `Double` value under [name].
 *
 * @param name The label shown next to the value.
 * @param getter Reads the current value. It runs often, so keep it cheap and allocation free.
 */
inline fun FlixelDebugWatches.double(name: String, crossinline getter: () -> Double) {
  addDouble(name) { getter() }
}

/**
 * Watches a `Boolean` value under [name].
 *
 * @param name The label shown next to the value.
 * @param getter Reads the current value. It runs often, so keep it cheap and allocation free.
 */
inline fun FlixelDebugWatches.boolean(name: String, crossinline getter: () -> Boolean) {
  addBoolean(name) { getter() }
}

/**
 * Watches any value under [name], shown with its `toString()`.
 *
 * Prefer the typed functions such as [float] for numbers: this one boxes primitives, which
 * allocates.
 *
 * @param name The label shown next to the value.
 * @param getter Reads the current value. It runs often, so keep it cheap.
 */
inline fun FlixelDebugWatches.value(name: String, crossinline getter: () -> Any?) {
  add(name) { getter() }
}

/**
 * Returns whether a watch named [name] exists, so you can write `"Score" in watches`.
 *
 * @param name The watch's label.
 * @return `true` if the watch exists.
 */
operator fun FlixelDebugWatches.contains(name: String): Boolean = has(name)

/**
 * Removes the watch named [name], so you can write `watches -= "Score"`.
 *
 * Does nothing if no such watch exists.
 *
 * @param name The watch's label.
 */
operator fun FlixelDebugWatches.minusAssign(name: String) {
  remove(name)
}
