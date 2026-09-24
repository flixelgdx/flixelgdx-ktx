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
@file:JvmName("FlixelUiContainers")

package org.flixelgdx.ktx.ui

import org.flixelgdx.ui.FlixelUiContainer
import org.flixelgdx.ui.FlixelUiDropdown
import org.flixelgdx.ui.FlixelUiWidget

/**
 * Layout helpers and collection-style operators for FlixelGDX UI containers and widgets.
 *
 * A container reads like a list of widgets: `panel += button`, `panel -= button`, `panel[0]`, and
 * `button in panel`. A dropdown reads like a list of item strings. Iterate children with
 * [forEachChild] rather than a `for` loop, since it walks the children by index and never creates
 * an iterator.
 */

/**
 * Anchors this widget to [align] inside its parent, with optional pixel offsets.
 *
 * This is `anchor(align, offsetX, offsetY)` with both offsets defaulting to `0`, so the common case
 * reads `anchor(FlixelAlign.CENTER)`.
 *
 * @param align The `FlixelAlign` flags to anchor to.
 * @param offsetX Pixels added to the anchored X.
 * @param offsetY Pixels added to the anchored Y.
 */
fun FlixelUiWidget.anchor(align: Int, offsetX: Float = 0f, offsetY: Float = 0f) {
  anchor(align, offsetX, offsetY)
}

/**
 * The padding on all four sides of this container's content area, as one value.
 *
 * Java has no single padding getter, since each side can differ, so this property exists only in
 * Kotlin. Setting it pads every side equally, like `setPadding(all)`. Reading it returns that
 * shared value, or [Float.NaN] when the sides differ (read `paddingLeft` and the other sides in
 * that case).
 */
var FlixelUiContainer.padding: Float
  get() {
    val left = paddingLeft
    val uniform = left == paddingTop && left == paddingRight && left == paddingBottom
    return if (uniform) left else Float.NaN
  }
  set(value) = setPadding(value)

/**
 * Sets the padding on the left and right sides to [horizontal] and on the top and bottom to
 * [vertical].
 *
 * @param horizontal Pixels of padding on the left and right.
 * @param vertical Pixels of padding on the top and bottom.
 */
fun FlixelUiContainer.padding(horizontal: Float, vertical: Float) =
  setPadding(horizontal, vertical, horizontal, vertical)

/** Adds [widget] as the last (topmost) child, so `container += widget` reads naturally. */
operator fun FlixelUiContainer.plusAssign(widget: FlixelUiWidget) = add(widget)

/** Removes [widget] from this container; does nothing when it is not a child. */
operator fun FlixelUiContainer.minusAssign(widget: FlixelUiWidget) {
  remove(widget)
}

/**
 * Returns the child at [index], in draw order.
 *
 * @param index The zero-based child index.
 * @return The child widget.
 */
operator fun FlixelUiContainer.get(index: Int): FlixelUiWidget = getChildAt(index)

/** Returns whether [widget] is a direct child of this container. */
operator fun FlixelUiContainer.contains(widget: FlixelUiWidget): Boolean = widget.parent === this

/** The number of direct children, the same as `childCount`. */
val FlixelUiContainer.size: Int
  get() = childCount

/**
 * Runs [action] on each direct child, in draw order, without allocating an iterator.
 *
 * Do not add or remove children of this container from inside [action].
 *
 * @param action Runs once per child.
 */
inline fun FlixelUiContainer.forEachChild(action: (FlixelUiWidget) -> Unit) {
  for (i in 0 until childCount) {
    action(getChildAt(i))
  }
}

/** Adds [text] as the last item, so `dropdown += "Ultra"` reads naturally. */
operator fun FlixelUiDropdown.plusAssign(text: CharSequence) = addItem(text)

/**
 * Returns the item text at [index].
 *
 * @param index The zero-based item index.
 * @return The item's text.
 */
operator fun FlixelUiDropdown.get(index: Int): CharSequence = getItem(index)

/**
 * Adds each of [texts] as an item, in order.
 *
 * @param texts The item texts to add.
 */
fun FlixelUiDropdown.items(vararg texts: CharSequence) {
  for (text in texts) {
    addItem(text)
  }
}
