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
@file:JvmName("FlixelDebugPropertyGrids")

package org.flixelgdx.ktx.debug

import org.flixelgdx.debug.inspector.FlixelPropertyBuilder
import org.flixelgdx.debug.ui.FlixelDebugProperties
import org.flixelgdx.util.FlixelString

/**
 * Adds a foldable section of live rows to this property grid.
 *
 * This is [FlixelDebugProperties.section] with the rows written as a block. It has a different name
 * because Kotlin always prefers the Java member, which takes a plain lambda with `it`:
 * ```kotlin
 * val grid = FlixelDebugProperties()
 *   .group("Enemy AI") {
 *     int("Active enemies") { enemies.countLiving() }
 *     float("Aggression", { ai.aggression }) { ai.aggression = it }
 *     boolean("Paused", { ai.paused }) { ai.paused = it }
 *   }
 *   .group("World") { double("Time") { world.time } }
 * ```
 *
 * The row functions are `inline` with `crossinline` getters and setters, so reading a row every
 * frame never boxes a number.
 *
 * @param title The section's header.
 * @param build Adds the section's rows.
 * @return This grid, for chaining.
 */
inline fun FlixelDebugProperties.group(
  title: String,
  crossinline build: FlixelPropertyBuilder.() -> Unit,
): FlixelDebugProperties = section(title) { it.build() }

/**
 * Marks the row added last as read once when [once] is `true`.
 *
 * Public only so the inline row functions can call it; it is not part of the API.
 */
@PublishedApi
internal fun FlixelPropertyBuilder.onceIf(once: Boolean): FlixelPropertyBuilder =
  if (once) once() else this

/**
 * Adds a read-only `Int` row.
 *
 * @param name The row's label.
 * @param once Whether to read the value only once, for values that never change.
 * @param get Reads the current value.
 * @return This builder, for chaining.
 */
inline fun FlixelPropertyBuilder.int(
  name: String,
  once: Boolean = false,
  crossinline get: () -> Int,
): FlixelPropertyBuilder = intProp(name) { get() }.onceIf(once)

/**
 * Adds an editable `Int` row. Edits made in the app call [set].
 *
 * @param name The row's label.
 * @param get Reads the current value.
 * @param once Whether to read the value only once.
 * @param set Applies an edited value.
 * @return This builder, for chaining.
 */
inline fun FlixelPropertyBuilder.int(
  name: String,
  crossinline get: () -> Int,
  once: Boolean = false,
  crossinline set: (Int) -> Unit,
): FlixelPropertyBuilder = intProp(name, { get() }, { set(it) }).onceIf(once)

/**
 * Adds a read-only `Long` row.
 *
 * @param name The row's label.
 * @param once Whether to read the value only once, for values that never change.
 * @param get Reads the current value.
 * @return This builder, for chaining.
 */
inline fun FlixelPropertyBuilder.long(
  name: String,
  once: Boolean = false,
  crossinline get: () -> Long,
): FlixelPropertyBuilder = longProp(name) { get() }.onceIf(once)

/**
 * Adds an editable `Long` row. Edits made in the app call [set].
 *
 * @param name The row's label.
 * @param get Reads the current value.
 * @param once Whether to read the value only once.
 * @param set Applies an edited value.
 * @return This builder, for chaining.
 */
inline fun FlixelPropertyBuilder.long(
  name: String,
  crossinline get: () -> Long,
  once: Boolean = false,
  crossinline set: (Long) -> Unit,
): FlixelPropertyBuilder = longProp(name, { get() }, { set(it) }).onceIf(once)

/**
 * Adds a read-only `Float` row.
 *
 * @param name The row's label.
 * @param once Whether to read the value only once, for values that never change.
 * @param get Reads the current value.
 * @return This builder, for chaining.
 */
inline fun FlixelPropertyBuilder.float(
  name: String,
  once: Boolean = false,
  crossinline get: () -> Float,
): FlixelPropertyBuilder = floatProp(name) { get() }.onceIf(once)

/**
 * Adds an editable `Float` row. Edits made in the app call [set].
 *
 * @param name The row's label.
 * @param get Reads the current value.
 * @param once Whether to read the value only once.
 * @param set Applies an edited value.
 * @return This builder, for chaining.
 */
inline fun FlixelPropertyBuilder.float(
  name: String,
  crossinline get: () -> Float,
  once: Boolean = false,
  crossinline set: (Float) -> Unit,
): FlixelPropertyBuilder = floatProp(name, { get() }, { set(it) }).onceIf(once)

/**
 * Adds a read-only `Double` row.
 *
 * @param name The row's label.
 * @param once Whether to read the value only once, for values that never change.
 * @param get Reads the current value.
 * @return This builder, for chaining.
 */
inline fun FlixelPropertyBuilder.double(
  name: String,
  once: Boolean = false,
  crossinline get: () -> Double,
): FlixelPropertyBuilder = doubleProp(name) { get() }.onceIf(once)

/**
 * Adds an editable `Double` row. Edits made in the app call [set].
 *
 * @param name The row's label.
 * @param get Reads the current value.
 * @param once Whether to read the value only once.
 * @param set Applies an edited value.
 * @return This builder, for chaining.
 */
inline fun FlixelPropertyBuilder.double(
  name: String,
  crossinline get: () -> Double,
  once: Boolean = false,
  crossinline set: (Double) -> Unit,
): FlixelPropertyBuilder = doubleProp(name, { get() }, { set(it) }).onceIf(once)

/**
 * Adds a read-only `Boolean` row.
 *
 * @param name The row's label.
 * @param once Whether to read the value only once, for values that never change.
 * @param get Reads the current value.
 * @return This builder, for chaining.
 */
inline fun FlixelPropertyBuilder.boolean(
  name: String,
  once: Boolean = false,
  crossinline get: () -> Boolean,
): FlixelPropertyBuilder = boolProp(name) { get() }.onceIf(once)

/**
 * Adds an editable `Boolean` row. Edits made in the app call [set].
 *
 * @param name The row's label.
 * @param get Reads the current value.
 * @param once Whether to read the value only once.
 * @param set Applies an edited value.
 * @return This builder, for chaining.
 */
inline fun FlixelPropertyBuilder.boolean(
  name: String,
  crossinline get: () -> Boolean,
  once: Boolean = false,
  crossinline set: (Boolean) -> Unit,
): FlixelPropertyBuilder = boolProp(name, { get() }, { set(it) }).onceIf(once)

/**
 * Adds a read-only text row.
 *
 * Text is appended into a reused buffer instead of returned, so building it does not allocate a new
 * string every frame.
 *
 * @param name The row's label.
 * @param once Whether to read the value only once.
 * @param get Appends the current text to the buffer.
 * @return This builder, for chaining.
 */
inline fun FlixelPropertyBuilder.text(
  name: String,
  once: Boolean = false,
  crossinline get: (FlixelString) -> Unit,
): FlixelPropertyBuilder = stringProp(name) { get(it) }.onceIf(once)

/**
 * Adds an editable text row. Edits made in the app call [set].
 *
 * @param name The row's label.
 * @param get Appends the current text to the buffer.
 * @param once Whether to read the value only once.
 * @param set Applies the edited text.
 * @return This builder, for chaining.
 */
inline fun FlixelPropertyBuilder.text(
  name: String,
  crossinline get: (FlixelString) -> Unit,
  once: Boolean = false,
  crossinline set: (CharSequence) -> Unit,
): FlixelPropertyBuilder = stringProp(name, { get(it) }, { set(it) }).onceIf(once)

/**
 * Adds a read-only row that shows an enum constant's name.
 *
 * @param name The row's label.
 * @param once Whether to read the value only once.
 * @param get Reads the current constant.
 * @return This builder, for chaining.
 */
inline fun FlixelPropertyBuilder.enum(
  name: String,
  once: Boolean = false,
  crossinline get: () -> Enum<*>?,
): FlixelPropertyBuilder = enumProp(name) { get() }.onceIf(once)
