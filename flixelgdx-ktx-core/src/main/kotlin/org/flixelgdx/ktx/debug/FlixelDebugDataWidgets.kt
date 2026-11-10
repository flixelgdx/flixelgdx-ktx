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
@file:JvmName("FlixelDebugDataWidgets")

package org.flixelgdx.ktx.debug

import org.flixelgdx.debug.remote.FlixelDebugProtocol
import org.flixelgdx.debug.ui.FlixelDebugCanvas
import org.flixelgdx.debug.ui.FlixelDebugTable
import org.flixelgdx.debug.ui.FlixelDebugTree

/**
 * Adds a column, with its options as named flags instead of `COLUMN_*` bits.
 *
 * ```kotlin
 * table.column("Name").column("Size", 90f, alignRight = true, mono = true)
 * ```
 *
 * @param title The column's header.
 * @param width The width in pixels, or `0` to share the leftover space.
 * @param sortable Whether clicking the header sorts the rows.
 * @param alignRight Whether to right-align the cells, which suits numbers.
 * @param mono Whether to draw the cells in a monospace font.
 * @return This table, for chaining.
 */
fun FlixelDebugTable.column(
  title: String,
  width: Float,
  sortable: Boolean = true,
  alignRight: Boolean = false,
  mono: Boolean = false,
): FlixelDebugTable {
  var flags = 0
  if (sortable) flags = flags or FlixelDebugProtocol.COLUMN_SORTABLE
  if (alignRight) flags = flags or FlixelDebugProtocol.COLUMN_RIGHT
  if (mono) flags = flags or FlixelDebugProtocol.COLUMN_MONO
  return column(title, width, flags)
}

/**
 * Replaces every row of this table with the rows added in [block].
 *
 * This wraps [FlixelDebugTable.beginRows] and [FlixelDebugTable.endRows], so the closing call can
 * never be forgotten. Rebuilding every frame is fine: the new rows are compared byte by byte with
 * the old ones, and nothing is sent when they match.
 *
 * ```kotlin
 * table.rows {
 *   for (enemy in enemies) row(enemy.id) { cell(enemy.name); cell(enemy.health) }
 * }
 * ```
 *
 * @param block Adds the rows.
 * @return `true` if the rows changed.
 */
inline fun FlixelDebugTable.rows(block: FlixelDebugTable.() -> Unit): Boolean {
  beginRows()
  block()
  return endRows()
}

/**
 * Adds a row inside [rows] and fills its cells with [block].
 *
 * @param id The id reported back when the row is selected or activated. Not its position.
 * @param tone The color of the row's text.
 * @param block Adds the row's cells, in column order.
 */
inline fun FlixelDebugTable.row(
  id: Long,
  tone: FlixelDebugTone = FlixelDebugTone.NORMAL,
  block: FlixelDebugTable.Row.() -> Unit,
) {
  row(id, tone.code).block()
}

/**
 * Replaces every node of this tree with the nodes added in [block].
 *
 * This wraps [FlixelDebugTree.beginNodes] and [FlixelDebugTree.endNodes]. Like [rows], it is cheap
 * to call every frame.
 *
 * @param block Adds the nodes, parents before their children.
 * @return `true` if the nodes changed.
 */
inline fun FlixelDebugTree.nodes(block: FlixelDebugTree.() -> Unit): Boolean {
  beginNodes()
  block()
  return endNodes()
}

/**
 * Adds a node inside [nodes] and configures it with [block].
 *
 * ```kotlin
 * tree.nodes {
 *   node(1, -1, "Player") { badge("agent", FlixelDebugTone.ACCENT) }
 *   node(2, 1, "Weapon") { detail("Pistol") }
 * }
 * ```
 *
 * @param id The id reported back when the node is selected or activated.
 * @param parent The id of the parent node, or `-1` for a top-level node.
 * @param text The node's label.
 * @param block Sets the node's detail, tone, and badges.
 */
inline fun FlixelDebugTree.node(
  id: Long,
  parent: Long,
  text: CharSequence?,
  block: FlixelDebugTree.Node.() -> Unit,
) {
  node(id, parent, text).block()
}

/**
 * Sets the color of this node's label. Call it before adding badges.
 *
 * @param tone The color to use.
 * @return This node, for chaining.
 */
fun FlixelDebugTree.Node.tone(tone: FlixelDebugTone): FlixelDebugTree.Node = style(tone.code)

/**
 * Adds a small colored tag after this node's label.
 *
 * @param text The tag's text.
 * @param tone The tag's color.
 * @return This node, for chaining.
 */
fun FlixelDebugTree.Node.badge(text: CharSequence, tone: FlixelDebugTone): FlixelDebugTree.Node =
  badge(text, tone.code)

/**
 * Replaces every shape on this canvas with the shapes added in [block].
 *
 * This wraps [FlixelDebugCanvas.beginShapes] and [FlixelDebugCanvas.endShapes].
 *
 * @param block Adds the shapes, bottom first.
 * @return `true` if the shapes changed.
 */
inline fun FlixelDebugCanvas.shapes(block: FlixelDebugCanvas.() -> Unit): Boolean {
  beginShapes()
  block()
  return endShapes()
}

/**
 * Adds a box inside [shapes], with its look as named options instead of style bits.
 *
 * @param id The id reported back when the box is picked or dragged.
 * @param x The left edge, in canvas units.
 * @param y The top edge, in canvas units.
 * @param width The box's width.
 * @param height The box's height.
 * @param tone The box's color.
 * @param label Text drawn with the box, or `null` for none.
 * @param selected Whether to draw the box as selected.
 * @param dashed Whether to draw a dashed outline.
 * @param dim Whether to draw the box faded.
 * @param filled Whether to fill the box instead of only outlining it.
 */
fun FlixelDebugCanvas.shape(
  id: Long,
  x: Float,
  y: Float,
  width: Float,
  height: Float,
  tone: FlixelDebugTone = FlixelDebugTone.NORMAL,
  label: CharSequence? = null,
  selected: Boolean = false,
  dashed: Boolean = false,
  dim: Boolean = false,
  filled: Boolean = false,
) {
  var style = tone.code
  if (selected) style = style or FlixelDebugProtocol.SHAPE_SELECTED
  if (dashed) style = style or FlixelDebugProtocol.SHAPE_DASHED
  if (dim) style = style or FlixelDebugProtocol.SHAPE_DIM
  if (filled) style = style or FlixelDebugProtocol.SHAPE_FILLED
  shape(id, x, y, width, height, style, label)
}

/**
 * Sets what the person can do with this canvas, as named flags instead of `INTERACT_*` bits.
 *
 * @param pick Whether clicking reports a pick.
 * @param drag Whether boxes can be dragged.
 * @param pan Whether the view can be panned.
 * @param zoom Whether the view can be zoomed.
 * @return This canvas, for chaining.
 */
fun FlixelDebugCanvas.setInteract(
  pick: Boolean = false,
  drag: Boolean = false,
  pan: Boolean = false,
  zoom: Boolean = false,
): FlixelDebugCanvas {
  var flags = 0
  if (pick) flags = flags or FlixelDebugProtocol.INTERACT_PICK
  if (drag) flags = flags or FlixelDebugProtocol.INTERACT_DRAG
  if (pan) flags = flags or FlixelDebugProtocol.INTERACT_PAN
  if (zoom) flags = flags or FlixelDebugProtocol.INTERACT_ZOOM
  return setInteract(flags)
}
