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
@file:JvmName("FlixelDebugConsoles")

package org.flixelgdx.ktx.debug

import org.flixelgdx.debug.FlixelDebugCommandArgs
import org.flixelgdx.debug.feeds.FlixelDebugConsole

/**
 * Adds a console command, with named and optional help text.
 *
 * This is [FlixelDebugConsole.register] with the usage and description as optional named arguments,
 * so a command reads in the order you think of it:
 * ```kotlin
 * Flixel.debug.console.command("god", usage = "god [on|off]", description = "Makes the player invincible.") { args ->
 *   player.invincible = args.boolean(0, !player.invincible)
 * }
 * ```
 *
 * Registering a name that already exists replaces the old command.
 *
 * @param name The word typed to run the command.
 * @param usage How to call it, shown in the app's completions, or `null` for none.
 * @param description What the command does, or `null` for none.
 * @param handler Runs the command with the words typed after its name.
 */
inline fun FlixelDebugConsole.command(
  name: String,
  usage: String? = null,
  description: String? = null,
  crossinline handler: (args: FlixelDebugCommandArgs) -> Unit,
) {
  register(name, usage, description) { handler(it) }
}

/**
 * Returns whether a command named [name] is registered, so you can write `"god" in console`.
 *
 * @param name The command name.
 * @return `true` if the command exists.
 */
operator fun FlixelDebugConsole.contains(name: String): Boolean = has(name)

/**
 * Removes the command named [name], so you can write `console -= "god"`.
 *
 * Does nothing if no such command exists.
 *
 * @param name The command name.
 */
operator fun FlixelDebugConsole.minusAssign(name: String) = unregister(name)

/** The number of words typed after the command name. */
val FlixelDebugCommandArgs.size: Int
  get() = argumentCount

/**
 * Returns the word at [index], or `null` when fewer words were typed.
 *
 * Unlike [FlixelDebugCommandArgs.getString], which returns an empty string for a missing word, this
 * lets you use Kotlin's `?:` to pick a fallback.
 *
 * @param index The zero-based position after the command name.
 * @return The word, or `null` if it is missing.
 */
operator fun FlixelDebugCommandArgs.get(index: Int): String? = getString(index, null)

/**
 * Returns the word at [index] as an `Int`, or [default] when it is missing or not a number.
 *
 * @param index The zero-based position after the command name.
 * @param default The value to use when the word is missing or invalid.
 * @return The parsed number, or [default].
 */
fun FlixelDebugCommandArgs.int(index: Int, default: Int = 0): Int = getInt(index, default)

/**
 * Returns the word at [index] as a `Long`, or [default] when it is missing or not a number.
 *
 * @param index The zero-based position after the command name.
 * @param default The value to use when the word is missing or invalid.
 * @return The parsed number, or [default].
 */
fun FlixelDebugCommandArgs.long(index: Int, default: Long = 0L): Long = getLong(index, default)

/**
 * Returns the word at [index] as a `Float`, or [default] when it is missing or not a number.
 *
 * @param index The zero-based position after the command name.
 * @param default The value to use when the word is missing or invalid.
 * @return The parsed number, or [default].
 */
fun FlixelDebugCommandArgs.float(index: Int, default: Float = 0f): Float = getFloat(index, default)

/**
 * Returns the word at [index] as a `Double`, or [default] when it is missing or not a number.
 *
 * @param index The zero-based position after the command name.
 * @param default The value to use when the word is missing or invalid.
 * @return The parsed number, or [default].
 */
fun FlixelDebugCommandArgs.double(index: Int, default: Double = 0.0): Double =
  getDouble(index, default)

/**
 * Returns the word at [index] as a `Boolean`, or [default] when it is missing or not recognized.
 *
 * `true`, `1`, `yes`, and `on` read as `true`; `false`, `0`, `no`, and `off` read as `false`,
 * ignoring case.
 *
 * @param index The zero-based position after the command name.
 * @param default The value to use when the word is missing or not recognized.
 * @return The parsed value, or [default].
 */
fun FlixelDebugCommandArgs.boolean(index: Int, default: Boolean = false): Boolean =
  getBoolean(index, default)
