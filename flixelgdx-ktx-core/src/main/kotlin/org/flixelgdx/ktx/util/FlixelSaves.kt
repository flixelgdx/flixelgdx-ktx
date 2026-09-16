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
@file:JvmName("FlixelSaves")

package org.flixelgdx.ktx.util

import kotlin.reflect.KProperty
import org.flixelgdx.Flixel
import org.flixelgdx.util.save.FlixelSave

/**
 * Property delegates that bind a Kotlin property to a key in the global [FlixelSave].
 *
 * Each delegate reads from [FlixelSave]'s typed getter on every access and writes back into the
 * in-memory [FlixelSave.data] map on every assignment. The property name is used as the save key
 * automatically, so no string literal is needed at the call site.
 *
 * These delegates deliberately do not touch the disk: writing a property only updates the in-memory
 * map. Call [FlixelSave.flush] yourself when you actually want to persist, so a hot-path assignment
 * never triggers a synchronous disk write (which would stutter the game loop). A settings screen,
 * for example, might mutate several delegated properties and flush once when the player backs out.
 *
 * This is reflection-free: [KProperty] is a compile-time Kotlin metadata object, not a JVM
 * reflection handle. No `java.lang.reflect` API is used.
 *
 * Example:
 * ```
 * var masterVolume: Float by save(1.0f)
 * var musicEnabled: Boolean by save(true)
 * var playerName: String by save("Player")
 *
 * // ...after the player changes some settings:
 * masterVolume = 0.5f
 * musicEnabled = false
 * Flixel.save.flush() // Persist the accumulated changes to disk once.
 * ```
 *
 * Bind the global save before accessing any delegated property:
 * ```
 * Flixel.save.bind("MyGame", "slot1")
 * ```
 */

/**
 * Delegate that stores and retrieves an [Int] in [Flixel.save] under the property name.
 *
 * @property default Value returned when the key is absent from the save.
 */
class IntSaveDelegate(private val default: Int) {

  /**
   * Reads the value from [Flixel.save] using the property name as the key.
   *
   * @param thisRef The object that owns the delegated property.
   * @param property The property metadata; its name is used as the save key.
   * @return The stored int, or [default] when the key is absent.
   */
  operator fun getValue(thisRef: Any?, property: KProperty<*>): Int {
    val save: FlixelSave = Flixel.save
    return save.getInt(property.name, default)
  }

  /**
   * Writes [value] into the in-memory [Flixel.save] under the property name. Call
   * [FlixelSave.flush] to persist it to disk.
   *
   * @param thisRef The object that owns the delegated property.
   * @param property The property metadata; its name is used as the save key.
   * @param value The new value to store.
   */
  operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
    Flixel.save.data.put(property.name, value)
  }
}

/**
 * Delegate that stores and retrieves a [Float] in [Flixel.save] under the property name.
 *
 * @property default Value returned when the key is absent from the save.
 */
class FloatSaveDelegate(private val default: Float) {

  /**
   * Reads the value from [Flixel.save] using the property name as the key.
   *
   * @param thisRef The object that owns the delegated property.
   * @param property The property metadata; its name is used as the save key.
   * @return The stored float, or [default] when the key is absent.
   */
  operator fun getValue(thisRef: Any?, property: KProperty<*>): Float =
    Flixel.save.getFloat(property.name, default)

  /**
   * Writes [value] into the in-memory [Flixel.save] under the property name. Call
   * [FlixelSave.flush] to persist it to disk.
   *
   * @param thisRef The object that owns the delegated property.
   * @param property The property metadata; its name is used as the save key.
   * @param value The new value to store.
   */
  operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Float) {
    Flixel.save.data.put(property.name, value)
  }
}

/**
 * Delegate that stores and retrieves a [Boolean] in [Flixel.save] under the property name.
 *
 * @property default Value returned when the key is absent from the save.
 */
class BoolSaveDelegate(private val default: Boolean) {

  /**
   * Reads the value from [Flixel.save] using the property name as the key.
   *
   * @param thisRef The object that owns the delegated property.
   * @param property The property metadata; its name is used as the save key.
   * @return The stored boolean, or [default] when the key is absent.
   */
  operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean =
    Flixel.save.getBool(property.name, default)

  /**
   * Writes [value] into the in-memory [Flixel.save] under the property name. Call
   * [FlixelSave.flush] to persist it to disk.
   *
   * @param thisRef The object that owns the delegated property.
   * @param property The property metadata; its name is used as the save key.
   * @param value The new value to store.
   */
  operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
    Flixel.save.data.put(property.name, value)
  }
}

/**
 * Delegate that stores and retrieves a [String] in [Flixel.save] under the property name.
 *
 * @property default Value returned when the key is absent from the save.
 */
class StringSaveDelegate(private val default: String) {

  /**
   * Reads the value from [Flixel.save] using the property name as the key.
   *
   * @param thisRef The object that owns the delegated property.
   * @param property The property metadata; its name is used as the save key.
   * @return The stored string, or [default] when the key is absent.
   */
  operator fun getValue(thisRef: Any?, property: KProperty<*>): String =
    Flixel.save.getString(property.name, default) ?: default

  /**
   * Writes [value] into the in-memory [Flixel.save] under the property name. Call
   * [FlixelSave.flush] to persist it to disk.
   *
   * @param thisRef The object that owns the delegated property.
   * @param property The property metadata; its name is used as the save key.
   * @param value The new value to store.
   */
  operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
    Flixel.save.data.put(property.name, value)
  }
}

/**
 * Creates an [IntSaveDelegate] with the given [default] value.
 *
 * @param default Value returned when no entry exists for this property's name.
 * @return A delegate backed by [Flixel.save].
 */
fun save(default: Int): IntSaveDelegate = IntSaveDelegate(default)

/**
 * Creates a [FloatSaveDelegate] with the given [default] value.
 *
 * @param default Value returned when no entry exists for this property's name.
 * @return A delegate backed by [Flixel.save].
 */
fun save(default: Float): FloatSaveDelegate = FloatSaveDelegate(default)

/**
 * Creates a [BoolSaveDelegate] with the given [default] value.
 *
 * @param default Value returned when no entry exists for this property's name.
 * @return A delegate backed by [Flixel.save].
 */
fun save(default: Boolean): BoolSaveDelegate = BoolSaveDelegate(default)

/**
 * Creates a [StringSaveDelegate] with the given [default] value.
 *
 * @param default Value returned when no entry exists for this property's name.
 * @return A delegate backed by [Flixel.save].
 */
fun save(default: String): StringSaveDelegate = StringSaveDelegate(default)
