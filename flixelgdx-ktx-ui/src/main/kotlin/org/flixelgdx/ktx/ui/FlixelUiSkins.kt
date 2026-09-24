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
@file:JvmName("FlixelUiSkins")

package org.flixelgdx.ktx.ui

import org.flixelgdx.file.FlixelFile
import org.flixelgdx.ui.graphics.FlixelNineSlice
import org.flixelgdx.ui.graphics.FlixelUiBackground
import org.flixelgdx.ui.graphics.FlixelUiColorFill
import org.flixelgdx.ui.skin.FlixelTextBoxStyle
import org.flixelgdx.ui.skin.FlixelUiButtonStyle
import org.flixelgdx.ui.skin.FlixelUiCheckboxStyle
import org.flixelgdx.ui.skin.FlixelUiDropdownStyle
import org.flixelgdx.ui.skin.FlixelUiLabelStyle
import org.flixelgdx.ui.skin.FlixelUiModalStyle
import org.flixelgdx.ui.skin.FlixelUiPanelStyle
import org.flixelgdx.ui.skin.FlixelUiRadioButtonStyle
import org.flixelgdx.ui.skin.FlixelUiSkin
import org.flixelgdx.ui.skin.FlixelUiStyle
import org.flixelgdx.ui.skin.FlixelUiTooltipStyle
import org.flixelgdx.util.FlixelColor

/**
 * A DSL for building [FlixelUiSkin]s, plus type-safe lookups and background helpers.
 *
 * ```kotlin
 * val skin = uiSkin {
 *   button { // Named "default".
 *     up = track(nineSlice(Flixel.files.internal("ui/button.png"), all = 6))
 *     over = track(nineSlice(Flixel.files.internal("ui/button_over.png"), all = 6))
 *     fontColor = FlixelColor(FlixelColor.WHITE)
 *   }
 *   button("danger") { up = track(colorFill(0xAA2222FF)) }
 *   label("title") { fontSize = 32 }
 * }
 *
 * val danger = skin.get<FlixelUiButtonStyle>("danger")
 * ```
 */

/** The style name widgets use until another one is set. */
const val DEFAULT_STYLE: String = "default"

/**
 * Creates a skin and fills it with the styles declared in [block].
 *
 * @param block Declares the skin's styles.
 * @return The new skin.
 */
inline fun uiSkin(block: FlixelUiSkinScope.() -> Unit): FlixelUiSkin {
  val skin = FlixelUiSkin()
  FlixelUiSkinScope(skin).block()
  return skin
}

/**
 * Adds more styles to this skin with the skin DSL.
 *
 * Widgets already on a display do not pick up the new styles by themselves; set the skin on the
 * display again (`ui.setSkin(skin)`) to restyle them.
 *
 * @param block Declares the styles to add.
 * @return This skin, for chaining.
 */
inline fun FlixelUiSkin.styles(block: FlixelUiSkinScope.() -> Unit): FlixelUiSkin {
  FlixelUiSkinScope(this).block()
  return this
}

/**
 * The receiver of [uiSkin] and [styles]: each function creates one style, runs its block, and
 * stores it in [skin] under the given name.
 *
 * Every function returns the style it stored, so a style can be kept and reused as the start of
 * another one. Inside a style block, [track] is still available, so a loaded background can be
 * handed to the skin to destroy later on the same line it is assigned.
 *
 * @property skin The skin the styles are stored in.
 */
class FlixelUiSkinScope(val skin: FlixelUiSkin) {

  /**
   * Hands [background] to the skin, which destroys it when the skin is destroyed.
   *
   * @param background The background to track, such as a nine-slice loaded from a file.
   * @return The same background.
   */
  fun <T : FlixelUiBackground> track(background: T): T = skin.track(background)

  /**
   * Configures [style] with [block] and stores it under [name]; the widget-specific functions below
   * are shorter forms of this.
   *
   * @param name The style name widgets refer to.
   * @param style The style object to store.
   * @param block Sets the style's fields.
   * @return The stored style.
   */
  inline fun <T : FlixelUiStyle> style(name: String, style: T, block: T.() -> Unit): T {
    style.block()
    skin.add(name, style)
    return style
  }

  /**
   * Declares a button style.
   *
   * @param name The style name, `"default"` unless given.
   * @param block Sets the style's fields.
   * @return The stored style.
   */
  inline fun button(
    name: String = DEFAULT_STYLE,
    block: FlixelUiButtonStyle.() -> Unit,
  ): FlixelUiButtonStyle = style(name, FlixelUiButtonStyle(), block)

  /**
   * Declares a label style.
   *
   * @param name The style name, `"default"` unless given.
   * @param block Sets the style's fields.
   * @return The stored style.
   */
  inline fun label(
    name: String = DEFAULT_STYLE,
    block: FlixelUiLabelStyle.() -> Unit,
  ): FlixelUiLabelStyle = style(name, FlixelUiLabelStyle(), block)

  /**
   * Declares a panel style.
   *
   * @param name The style name, `"default"` unless given.
   * @param block Sets the style's fields.
   * @return The stored style.
   */
  inline fun panel(
    name: String = DEFAULT_STYLE,
    block: FlixelUiPanelStyle.() -> Unit,
  ): FlixelUiPanelStyle = style(name, FlixelUiPanelStyle(), block)

  /**
   * Declares a modal style.
   *
   * @param name The style name, `"default"` unless given.
   * @param block Sets the style's fields.
   * @return The stored style.
   */
  inline fun modal(
    name: String = DEFAULT_STYLE,
    block: FlixelUiModalStyle.() -> Unit,
  ): FlixelUiModalStyle = style(name, FlixelUiModalStyle(), block)

  /**
   * Declares a checkbox style.
   *
   * @param name The style name, `"default"` unless given.
   * @param block Sets the style's fields.
   * @return The stored style.
   */
  inline fun checkbox(
    name: String = DEFAULT_STYLE,
    block: FlixelUiCheckboxStyle.() -> Unit,
  ): FlixelUiCheckboxStyle = style(name, FlixelUiCheckboxStyle(), block)

  /**
   * Declares a radio button style.
   *
   * @param name The style name, `"default"` unless given.
   * @param block Sets the style's fields.
   * @return The stored style.
   */
  inline fun radio(
    name: String = DEFAULT_STYLE,
    block: FlixelUiRadioButtonStyle.() -> Unit,
  ): FlixelUiRadioButtonStyle = style(name, FlixelUiRadioButtonStyle(), block)

  /**
   * Declares a dropdown style.
   *
   * @param name The style name, `"default"` unless given.
   * @param block Sets the style's fields.
   * @return The stored style.
   */
  inline fun dropdown(
    name: String = DEFAULT_STYLE,
    block: FlixelUiDropdownStyle.() -> Unit,
  ): FlixelUiDropdownStyle = style(name, FlixelUiDropdownStyle(), block)

  /**
   * Declares a text box style.
   *
   * @param name The style name, `"default"` unless given.
   * @param block Sets the style's fields.
   * @return The stored style.
   */
  inline fun textBox(
    name: String = DEFAULT_STYLE,
    block: FlixelTextBoxStyle.() -> Unit,
  ): FlixelTextBoxStyle = style(name, FlixelTextBoxStyle(), block)

  /**
   * Declares a tooltip style.
   *
   * @param name The style name, `"default"` unless given.
   * @param block Sets the style's fields.
   * @return The stored style.
   */
  inline fun tooltip(
    name: String = DEFAULT_STYLE,
    block: FlixelUiTooltipStyle.() -> Unit,
  ): FlixelUiTooltipStyle = style(name, FlixelUiTooltipStyle(), block)
}

/**
 * Returns the style of type [T] named [name].
 *
 * @param name The style name, `"default"` unless given.
 * @return The style.
 * @throws IllegalArgumentException If the skin has no such style.
 */
inline fun <reified T : FlixelUiStyle> FlixelUiSkin.get(name: String = DEFAULT_STYLE): T =
  get(T::class.java, name)

/**
 * Returns whether the skin has a style of type [T] named [name].
 *
 * @param name The style name, `"default"` unless given.
 * @return `true` when the style exists.
 */
inline fun <reified T : FlixelUiStyle> FlixelUiSkin.has(name: String = DEFAULT_STYLE): Boolean =
  has(T::class.java, name)

/**
 * Removes the style of type [T] named [name].
 *
 * @param name The style name, `"default"` unless given.
 * @return `true` when a style was removed.
 */
inline fun <reified T : FlixelUiStyle> FlixelUiSkin.remove(name: String = DEFAULT_STYLE): Boolean =
  remove(T::class.java, name)

/** Stores [style] under [name], so `skin["fab"] = FlixelUiButtonStyle()` reads naturally. */
operator fun FlixelUiSkin.set(name: String, style: FlixelUiStyle) = add(name, style)

/**
 * Loads a nine-slice background with the same inset on all four sides.
 *
 * @param file The image file.
 * @param all The inset, in pixels, that stays unstretched on every side.
 * @return The loaded nine-slice; pass it to `track(...)` in a skin so the skin destroys it.
 */
fun nineSlice(file: FlixelFile, all: Int): FlixelNineSlice =
  FlixelNineSlice.load(file, all, all, all, all)

/**
 * Loads a nine-slice background with one inset for the left and right sides and another for the top
 * and bottom.
 *
 * @param file The image file.
 * @param horizontal The inset, in pixels, on the left and right.
 * @param vertical The inset, in pixels, on the top and bottom.
 * @return The loaded nine-slice; pass it to `track(...)` in a skin so the skin destroys it.
 */
fun nineSlice(file: FlixelFile, horizontal: Int, vertical: Int): FlixelNineSlice =
  FlixelNineSlice.load(file, horizontal, vertical, horizontal, vertical)

/**
 * Creates a solid color background.
 *
 * @param color The fill color.
 * @return The new background.
 */
fun colorFill(color: FlixelColor): FlixelUiColorFill = FlixelUiColorFill(color)

/**
 * Creates a solid color background from a packed `0xRRGGBBAA` value.
 *
 * The parameter is a [Long] so hex literals with the top bit set, such as `0xAA2222FF`, can be
 * passed as they are; only the low 32 bits are used.
 *
 * @param rgba The fill color as `0xRRGGBBAA`.
 * @return The new background.
 */
fun colorFill(rgba: Long): FlixelUiColorFill = FlixelUiColorFill(FlixelColor(rgba.toInt()))
