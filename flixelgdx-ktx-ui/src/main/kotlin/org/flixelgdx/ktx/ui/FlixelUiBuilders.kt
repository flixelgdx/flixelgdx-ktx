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
@file:JvmName("FlixelUiBuilders")

package org.flixelgdx.ktx.ui

import org.flixelgdx.FlixelCamera
import org.flixelgdx.file.FlixelFile
import org.flixelgdx.ui.FlixelUiButton
import org.flixelgdx.ui.FlixelUiCheckbox
import org.flixelgdx.ui.FlixelUiContainer
import org.flixelgdx.ui.FlixelUiDisplay
import org.flixelgdx.ui.FlixelUiDropdown
import org.flixelgdx.ui.FlixelUiLabel
import org.flixelgdx.ui.FlixelUiModal
import org.flixelgdx.ui.FlixelUiPanel
import org.flixelgdx.ui.FlixelUiRadioButton
import org.flixelgdx.ui.FlixelUiRadioGroup
import org.flixelgdx.ui.FlixelUiStack
import org.flixelgdx.ui.FlixelUiWidget
import org.flixelgdx.ui.skin.FlixelUiSkin
import org.flixelgdx.ui.text.FlixelTextBox

/**
 * A builder DSL for FlixelGDX UI widget trees.
 *
 * In Java, building a menu means creating each widget, configuring it, and calling `add(...)` on
 * its parent. These builders let the shape of the code match the shape of the UI instead: each
 * function creates a widget, adds it to the container it is called in, runs the configuration block
 * on it, and returns it so a reference can be kept.
 *
 * ```kotlin
 * ui = uiDisplay(FlixelUiDisplay.createHudCamera(), skin) {
 *   vstack(spacing = 8f) {
 *     anchor(FlixelAlign.CENTER)
 *     label("Main Menu", style = "title")
 *     button("Play") { onClick { startGame() } }
 *     val quit = button("Quit", style = "danger")
 *   }
 * }
 * ```
 *
 * Every builder is `inline`, so the configuration blocks compile away. Only the signal handlers
 * registered inside them are real objects, and those are created once while the UI is built, never
 * per frame.
 *
 * Each widget is added to its parent before its block runs, so when the parent is already on a
 * display (as it is inside [uiDisplay] and [build]), the widget's style is resolved from the skin
 * right away and calls that need a display, such as `FlixelUiDropdown.open()`, work inside the
 * block.
 */

/**
 * Marks the receivers of the UI builder DSL so a block can only add to its own widget.
 *
 * Without it, calling `label(...)` inside a `button { }` block would quietly add the label to
 * whatever container the button sits in, because that container is still an implicit receiver. With
 * the marker, the compiler rejects the call instead. Reach an outer receiver on purpose with a
 * labeled `this`, such as `this@vstack`.
 */
@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE, AnnotationTarget.TYPEALIAS)
annotation class FlixelUiDsl

/**
 * Creates a display on [camera] and builds its root layer with [block].
 *
 * Pass a [skin] so styles can be resolved while the tree is built; without one the display uses an
 * empty skin, so every widget then needs a style object set directly.
 *
 * @param camera The camera the display draws on, usually from `FlixelUiDisplay.createHudCamera()`.
 * @param skin The skin to resolve styles from, or `null` for an empty one.
 * @param block Builds the widgets on the display's root layer.
 * @return The new display, ready to be added to a state.
 */
inline fun uiDisplay(
  camera: FlixelCamera,
  skin: FlixelUiSkin? = null,
  block: @FlixelUiDsl FlixelUiContainer.() -> Unit,
): FlixelUiDisplay {
  val display = if (skin != null) FlixelUiDisplay(camera, skin) else FlixelUiDisplay(camera)
  display.root.block()
  return display
}

/**
 * Adds more widgets to this display's root layer with the builder DSL.
 *
 * @param block Builds the widgets on the root layer.
 * @return This display, for chaining.
 */
inline fun FlixelUiDisplay.build(
  block: @FlixelUiDsl FlixelUiContainer.() -> Unit
): FlixelUiDisplay {
  root.block()
  return this
}

/**
 * Builds a modal with [block] and opens it on this display.
 *
 * Unlike the other builders, the block runs before the modal is added to the display, so handlers
 * registered on `onOpen` inside the block still see the modal open. The modal is destroyed when it
 * closes by default, because a modal built on the spot is rarely shown twice; pass `destroyOnClose
 * = false` to keep it and reopen it later with `openModal(...)`.
 *
 * @param width The modal's width in pixels.
 * @param height The modal's height in pixels.
 * @param style The style name to use, or `null` for `"default"`.
 * @param destroyOnClose Whether closing the modal destroys it.
 * @param block Builds the modal's contents.
 * @return The open modal.
 */
inline fun FlixelUiDisplay.modal(
  width: Float,
  height: Float,
  style: String? = null,
  destroyOnClose: Boolean = true,
  block: @FlixelUiDsl FlixelUiModal.() -> Unit,
): FlixelUiModal {
  val modal = FlixelUiModal(width, height)
  modal.destroyOnClose = destroyOnClose
  if (style != null) {
    modal.setStyle(style)
  }
  modal.block()
  openModal(modal)
  return modal
}

/**
 * Applies the options every builder shares, adds [widget] to this container, and runs [block].
 *
 * Public only so the inline builders can call it; it is not part of the API.
 */
@PublishedApi
internal inline fun <W : FlixelUiWidget> FlixelUiContainer.place(
  widget: W,
  style: String?,
  tooltip: CharSequence?,
  block: W.() -> Unit,
): W {
  if (style != null) {
    widget.setStyle(style)
  }
  if (tooltip != null) {
    widget.setTooltip(tooltip)
  }
  add(widget)
  widget.block()
  return widget
}

/**
 * Adds a plain, invisible container that only groups and positions its children.
 *
 * @param width The container's width in pixels.
 * @param height The container's height in pixels.
 * @param block Configures the container and builds its children.
 * @return The new container.
 */
inline fun FlixelUiContainer.container(
  width: Float = 0f,
  height: Float = 0f,
  block: @FlixelUiDsl FlixelUiContainer.() -> Unit = {},
): FlixelUiContainer = place(FlixelUiContainer(width, height), null, null, block)

/**
 * Adds a panel, a container with a styled background.
 *
 * @param width The panel's width in pixels.
 * @param height The panel's height in pixels.
 * @param style The style name to use, or `null` for `"default"`.
 * @param block Configures the panel and builds its children.
 * @return The new panel.
 */
inline fun FlixelUiContainer.panel(
  width: Float = 0f,
  height: Float = 0f,
  style: String? = null,
  block: @FlixelUiDsl FlixelUiPanel.() -> Unit = {},
): FlixelUiPanel = place(FlixelUiPanel(width, height), style, null, block)

/**
 * Adds a stack that lays its children out top to bottom.
 *
 * @param spacing Pixels between neighboring children.
 * @param align The `FlixelAlign` flags for the cross axis, or `null` to keep the default (left).
 * @param block Configures the stack and builds its children.
 * @return The new stack.
 */
inline fun FlixelUiContainer.vstack(
  spacing: Float = 0f,
  align: Int? = null,
  block: @FlixelUiDsl FlixelUiStack.() -> Unit = {},
): FlixelUiStack = stack(FlixelUiStack.Direction.VERTICAL, spacing, align, block)

/**
 * Adds a stack that lays its children out left to right.
 *
 * @param spacing Pixels between neighboring children.
 * @param align The `FlixelAlign` flags for the cross axis, or `null` to keep the default (top).
 * @param block Configures the stack and builds its children.
 * @return The new stack.
 */
inline fun FlixelUiContainer.hstack(
  spacing: Float = 0f,
  align: Int? = null,
  block: @FlixelUiDsl FlixelUiStack.() -> Unit = {},
): FlixelUiStack = stack(FlixelUiStack.Direction.HORIZONTAL, spacing, align, block)

/**
 * Adds a stack in the given [direction]; [vstack] and [hstack] are the shorter forms.
 *
 * @param direction Which way the children are laid out.
 * @param spacing Pixels between neighboring children.
 * @param align The `FlixelAlign` flags for the cross axis, or `null` to keep the default.
 * @param block Configures the stack and builds its children.
 * @return The new stack.
 */
inline fun FlixelUiContainer.stack(
  direction: FlixelUiStack.Direction,
  spacing: Float = 0f,
  align: Int? = null,
  block: @FlixelUiDsl FlixelUiStack.() -> Unit = {},
): FlixelUiStack {
  val stack = FlixelUiStack(direction)
  stack.spacing = spacing
  if (align != null) {
    stack.align = align
  }
  return place(stack, null, null, block)
}

/**
 * Adds a text label.
 *
 * @param text The text to show.
 * @param style The style name to use, or `null` for `"default"`.
 * @param block Configures the label.
 * @return The new label.
 */
inline fun FlixelUiContainer.label(
  text: CharSequence?,
  style: String? = null,
  block: @FlixelUiDsl FlixelUiLabel.() -> Unit = {},
): FlixelUiLabel = place(FlixelUiLabel(text), style, null, block)

/**
 * Adds a button with a text caption.
 *
 * @param text The caption, or `null` for none.
 * @param style The style name to use, or `null` for `"default"`.
 * @param tooltip The tooltip text, or `null` for none.
 * @param block Configures the button, usually registering an `onClick { }` handler.
 * @return The new button.
 */
inline fun FlixelUiContainer.button(
  text: CharSequence?,
  style: String? = null,
  tooltip: CharSequence? = null,
  block: @FlixelUiDsl FlixelUiButton.() -> Unit = {},
): FlixelUiButton = place(FlixelUiButton(text), style, tooltip, block)

/**
 * Adds a button that shows an icon, with an optional caption beside it.
 *
 * @param icon The icon image file.
 * @param text The caption, or `null` for an icon-only button.
 * @param style The style name to use, or `null` for `"default"`.
 * @param tooltip The tooltip text, or `null` for none.
 * @param block Configures the button, usually registering an `onClick { }` handler.
 * @return The new button.
 */
inline fun FlixelUiContainer.button(
  icon: FlixelFile,
  text: CharSequence? = null,
  style: String? = null,
  tooltip: CharSequence? = null,
  block: @FlixelUiDsl FlixelUiButton.() -> Unit = {},
): FlixelUiButton = place(FlixelUiButton(text, icon, null), style, tooltip, block)

/**
 * Adds a checkbox.
 *
 * @param label The text beside the box, or `null` for none.
 * @param checked Whether the box starts checked.
 * @param style The style name to use, or `null` for `"default"`.
 * @param tooltip The tooltip text, or `null` for none.
 * @param block Configures the checkbox.
 * @return The new checkbox.
 */
inline fun FlixelUiContainer.checkbox(
  label: CharSequence?,
  checked: Boolean = false,
  style: String? = null,
  tooltip: CharSequence? = null,
  block: @FlixelUiDsl FlixelUiCheckbox.() -> Unit = {},
): FlixelUiCheckbox = place(FlixelUiCheckbox(label, checked), style, tooltip, block)

/**
 * Adds a radio button that belongs to [group].
 *
 * The group is a plain object rather than a container, so the radio buttons of one group can sit in
 * different stacks or panels:
 * ```kotlin
 * val difficulty = FlixelUiRadioGroup()
 * hstack(spacing = 12f) {
 *   radio("Easy", difficulty)
 *   radio("Normal", difficulty)
 *   radio("Hard", difficulty)
 * }
 * difficulty.select(1)
 * ```
 *
 * @param label The text beside the button, or `null` for none.
 * @param group The group this button joins.
 * @param style The style name to use, or `null` for `"default"`.
 * @param tooltip The tooltip text, or `null` for none.
 * @param block Configures the radio button.
 * @return The new radio button.
 */
inline fun FlixelUiContainer.radio(
  label: CharSequence?,
  group: FlixelUiRadioGroup,
  style: String? = null,
  tooltip: CharSequence? = null,
  block: @FlixelUiDsl FlixelUiRadioButton.() -> Unit = {},
): FlixelUiRadioButton = place(FlixelUiRadioButton(label, group), style, tooltip, block)

/**
 * Adds a dropdown.
 *
 * ```kotlin
 * dropdown(160f) {
 *   items("Low", "Medium", "High")
 *   selectedIndex = 1
 * }
 * ```
 *
 * @param width The dropdown's width in pixels.
 * @param style The style name to use, or `null` for `"default"`.
 * @param tooltip The tooltip text, or `null` for none.
 * @param block Configures the dropdown and adds its items.
 * @return The new dropdown.
 */
inline fun FlixelUiContainer.dropdown(
  width: Float,
  style: String? = null,
  tooltip: CharSequence? = null,
  block: @FlixelUiDsl FlixelUiDropdown.() -> Unit = {},
): FlixelUiDropdown = place(FlixelUiDropdown(width), style, tooltip, block)

/**
 * Adds a text box.
 *
 * @param width The box's width in pixels.
 * @param height The box's height in pixels, or `null` to size it to one line of text.
 * @param style The style name to use, or `null` for `"default"`.
 * @param tooltip The tooltip text, or `null` for none.
 * @param block Configures the text box.
 * @return The new text box.
 */
inline fun FlixelUiContainer.textBox(
  width: Float,
  height: Float? = null,
  style: String? = null,
  tooltip: CharSequence? = null,
  block: @FlixelUiDsl FlixelTextBox.() -> Unit = {},
): FlixelTextBox {
  val box = if (height != null) FlixelTextBox(width, height) else FlixelTextBox(width)
  return place(box, style, tooltip, block)
}
