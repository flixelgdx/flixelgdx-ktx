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
@file:JvmName("FlixelDebugInspectors")

package org.flixelgdx.ktx.debug

import org.flixelgdx.debug.feeds.FlixelDebugInspector
import org.flixelgdx.debug.inspector.FlixelSectionBuilder
import org.flixelgdx.util.FlixelString

/**
 * Shows a section for every selected object of type [T] in the inspector.
 *
 * This is [FlixelDebugInspector.register] with the class taken from the type argument and the rows
 * written as a block:
 * ```kotlin
 * Flixel.debug.inspector.inspect<Player>("Player") {
 *   int("Health", Player::health) { p, v -> p.health = v } // Editable, because it has a setter.
 *   float("Speed") { it.speed }                              // Read-only.
 *   enum("State", get = Player::state)
 *   text("Name") { p, out -> out.concat(p.name) }
 *   int("Id", once = true, get = Player::id)                // Read once per selection.
 * }
 * ```
 *
 * The row functions are `inline` with `crossinline` getters and setters, so a property reference
 * such as `Player::health` compiles to a direct read, and numbers are never boxed.
 *
 * @param T The class to describe. Subclasses show this section too.
 * @param displayName The section's title in the inspector.
 * @param build Adds the section's rows.
 */
inline fun <reified T : Any> FlixelDebugInspector.inspect(
  displayName: String,
  crossinline build: FlixelSectionBuilder<T>.() -> Unit,
) {
  register(T::class.java, displayName) { it.build() }
}

/**
 * Shows a section that reads from an object related to the selected [T], such as its weapon.
 *
 * The section is hidden when [via] returns `null`:
 * ```kotlin
 * Flixel.debug.inspector.inspect("Weapon", via = Player::weapon) {
 *   int("Ammo", Weapon::ammo) { w, v -> w.ammo = v }
 * }
 * ```
 *
 * @param T The class whose selection shows the section.
 * @param R The type of the related object the rows read from.
 * @param displayName The section's title in the inspector.
 * @param via Finds the related object, or returns `null` to hide the section.
 * @param build Adds the section's rows.
 * @see FlixelSectionBuilder.find
 */
inline fun <reified T : Any, R> FlixelDebugInspector.inspect(
  displayName: String,
  crossinline via: (T) -> R?,
  crossinline build: FlixelSectionBuilder<R>.() -> Unit,
) {
  register(T::class.java, displayName) { builder -> builder.find<R> { via(it) }.build() }
}

/**
 * Removes the inspector section registered for [T].
 *
 * @return `true` if a section was removed.
 */
inline fun <reified T : Any> FlixelDebugInspector.unregister(): Boolean = unregister(T::class.java)

/**
 * Returns whether a section is registered for [T].
 *
 * @return `true` if [T] has a section.
 */
inline fun <reified T : Any> FlixelDebugInspector.isRegistered(): Boolean =
  isRegistered(T::class.java)

/**
 * Returns the selected object if it is a [T], or `null` otherwise.
 *
 * @return The selection as a [T], or `null`.
 */
inline fun <reified T : Any> FlixelDebugInspector.selectedAs(): T? = selected as? T

/**
 * Calls [listener] whenever the selection changes to an object of type [T].
 *
 * Selections of other types, and clearing the selection, are ignored. Keep the returned listener to
 * remove it later with [FlixelDebugInspector.removeSelectionListener].
 *
 * @param listener Runs with the newly selected [T].
 * @return The listener that was added.
 */
inline fun <reified T : Any> FlixelDebugInspector.onSelect(
  crossinline listener: (T) -> Unit
): FlixelDebugInspector.SelectionListener {
  val selectionListener =
    FlixelDebugInspector.SelectionListener { selected -> if (selected is T) listener(selected) }
  addSelectionListener(selectionListener)
  return selectionListener
}

/**
 * Marks the row added last as read once per selection when [once] is `true`.
 *
 * Public only so the inline row functions can call it; it is not part of the API.
 */
@PublishedApi
internal fun <T> FlixelSectionBuilder<T>.onceIf(once: Boolean): FlixelSectionBuilder<T> =
  if (once) once() else this

/**
 * Adds a read-only `Int` row.
 *
 * @param name The row's label.
 * @param once Whether to read the value only once per selection, for values that never change.
 * @param get Reads the value from the inspected object.
 * @return This builder, for chaining.
 */
inline fun <T> FlixelSectionBuilder<T>.int(
  name: String,
  once: Boolean = false,
  crossinline get: (T) -> Int,
): FlixelSectionBuilder<T> = intProp(name) { get(it) }.onceIf(once)

/**
 * Adds an editable `Int` row. Edits made in the app call [set].
 *
 * @param name The row's label.
 * @param get Reads the value from the inspected object.
 * @param once Whether to read the value only once per selection.
 * @param set Writes an edited value back to the inspected object.
 * @return This builder, for chaining.
 */
inline fun <T> FlixelSectionBuilder<T>.int(
  name: String,
  crossinline get: (T) -> Int,
  once: Boolean = false,
  crossinline set: (T, Int) -> Unit,
): FlixelSectionBuilder<T> =
  intProp(name, { get(it) }, { target, value -> set(target, value) }).onceIf(once)

/**
 * Adds a read-only `Long` row.
 *
 * @param name The row's label.
 * @param once Whether to read the value only once per selection, for values that never change.
 * @param get Reads the value from the inspected object.
 * @return This builder, for chaining.
 */
inline fun <T> FlixelSectionBuilder<T>.long(
  name: String,
  once: Boolean = false,
  crossinline get: (T) -> Long,
): FlixelSectionBuilder<T> = longProp(name) { get(it) }.onceIf(once)

/**
 * Adds an editable `Long` row. Edits made in the app call [set].
 *
 * @param name The row's label.
 * @param get Reads the value from the inspected object.
 * @param once Whether to read the value only once per selection.
 * @param set Writes an edited value back to the inspected object.
 * @return This builder, for chaining.
 */
inline fun <T> FlixelSectionBuilder<T>.long(
  name: String,
  crossinline get: (T) -> Long,
  once: Boolean = false,
  crossinline set: (T, Long) -> Unit,
): FlixelSectionBuilder<T> =
  longProp(name, { get(it) }, { target, value -> set(target, value) }).onceIf(once)

/**
 * Adds a read-only `Float` row.
 *
 * @param name The row's label.
 * @param once Whether to read the value only once per selection, for values that never change.
 * @param get Reads the value from the inspected object.
 * @return This builder, for chaining.
 */
inline fun <T> FlixelSectionBuilder<T>.float(
  name: String,
  once: Boolean = false,
  crossinline get: (T) -> Float,
): FlixelSectionBuilder<T> = floatProp(name) { get(it) }.onceIf(once)

/**
 * Adds an editable `Float` row. Edits made in the app call [set].
 *
 * @param name The row's label.
 * @param get Reads the value from the inspected object.
 * @param once Whether to read the value only once per selection.
 * @param set Writes an edited value back to the inspected object.
 * @return This builder, for chaining.
 */
inline fun <T> FlixelSectionBuilder<T>.float(
  name: String,
  crossinline get: (T) -> Float,
  once: Boolean = false,
  crossinline set: (T, Float) -> Unit,
): FlixelSectionBuilder<T> =
  floatProp(name, { get(it) }, { target, value -> set(target, value) }).onceIf(once)

/**
 * Adds a read-only `Double` row.
 *
 * @param name The row's label.
 * @param once Whether to read the value only once per selection, for values that never change.
 * @param get Reads the value from the inspected object.
 * @return This builder, for chaining.
 */
inline fun <T> FlixelSectionBuilder<T>.double(
  name: String,
  once: Boolean = false,
  crossinline get: (T) -> Double,
): FlixelSectionBuilder<T> = doubleProp(name) { get(it) }.onceIf(once)

/**
 * Adds an editable `Double` row. Edits made in the app call [set].
 *
 * @param name The row's label.
 * @param get Reads the value from the inspected object.
 * @param once Whether to read the value only once per selection.
 * @param set Writes an edited value back to the inspected object.
 * @return This builder, for chaining.
 */
inline fun <T> FlixelSectionBuilder<T>.double(
  name: String,
  crossinline get: (T) -> Double,
  once: Boolean = false,
  crossinline set: (T, Double) -> Unit,
): FlixelSectionBuilder<T> =
  doubleProp(name, { get(it) }, { target, value -> set(target, value) }).onceIf(once)

/**
 * Adds a read-only `Boolean` row.
 *
 * @param name The row's label.
 * @param once Whether to read the value only once per selection, for values that never change.
 * @param get Reads the value from the inspected object.
 * @return This builder, for chaining.
 */
inline fun <T> FlixelSectionBuilder<T>.boolean(
  name: String,
  once: Boolean = false,
  crossinline get: (T) -> Boolean,
): FlixelSectionBuilder<T> = boolProp(name) { get(it) }.onceIf(once)

/**
 * Adds an editable `Boolean` row. Edits made in the app call [set].
 *
 * @param name The row's label.
 * @param get Reads the value from the inspected object.
 * @param once Whether to read the value only once per selection.
 * @param set Writes an edited value back to the inspected object.
 * @return This builder, for chaining.
 */
inline fun <T> FlixelSectionBuilder<T>.boolean(
  name: String,
  crossinline get: (T) -> Boolean,
  once: Boolean = false,
  crossinline set: (T, Boolean) -> Unit,
): FlixelSectionBuilder<T> =
  boolProp(name, { get(it) }, { target, value -> set(target, value) }).onceIf(once)

/**
 * Adds a read-only text row.
 *
 * Text is appended into a reused buffer instead of returned, so building it does not allocate a new
 * string every frame.
 *
 * @param name The row's label.
 * @param once Whether to read the value only once per selection.
 * @param get Appends the text for the inspected object to the buffer.
 * @return This builder, for chaining.
 */
inline fun <T> FlixelSectionBuilder<T>.text(
  name: String,
  once: Boolean = false,
  crossinline get: (T, FlixelString) -> Unit,
): FlixelSectionBuilder<T> = stringProp(name) { target, out -> get(target, out) }.onceIf(once)

/**
 * Adds an editable text row. Edits made in the app call [set].
 *
 * @param name The row's label.
 * @param get Appends the text for the inspected object to the buffer.
 * @param once Whether to read the value only once per selection.
 * @param set Writes the edited text back to the inspected object.
 * @return This builder, for chaining.
 */
inline fun <T> FlixelSectionBuilder<T>.text(
  name: String,
  crossinline get: (T, FlixelString) -> Unit,
  once: Boolean = false,
  crossinline set: (T, CharSequence) -> Unit,
): FlixelSectionBuilder<T> =
  stringProp(name, { target, out -> get(target, out) }, { target, value -> set(target, value) })
    .onceIf(once)

/**
 * Adds a read-only row that shows an enum constant's name.
 *
 * @param name The row's label.
 * @param once Whether to read the value only once per selection.
 * @param get Reads the constant from the inspected object.
 * @return This builder, for chaining.
 */
inline fun <T> FlixelSectionBuilder<T>.enum(
  name: String,
  once: Boolean = false,
  crossinline get: (T) -> Enum<*>?,
): FlixelSectionBuilder<T> = enumProp(name) { get(it) }.onceIf(once)
