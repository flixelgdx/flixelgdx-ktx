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
@file:JvmName("FlixelDebugPanelBuilders")

package org.flixelgdx.ktx.debug

import org.flixelgdx.debug.ui.FlixelDebugButton
import org.flixelgdx.debug.ui.FlixelDebugCanvas
import org.flixelgdx.debug.ui.FlixelDebugCheckbox
import org.flixelgdx.debug.ui.FlixelDebugColorPicker
import org.flixelgdx.debug.ui.FlixelDebugContainer
import org.flixelgdx.debug.ui.FlixelDebugDropdown
import org.flixelgdx.debug.ui.FlixelDebugExpander
import org.flixelgdx.debug.ui.FlixelDebugGraph
import org.flixelgdx.debug.ui.FlixelDebugGrid
import org.flixelgdx.debug.ui.FlixelDebugImage
import org.flixelgdx.debug.ui.FlixelDebugLabel
import org.flixelgdx.debug.ui.FlixelDebugNumber
import org.flixelgdx.debug.ui.FlixelDebugPanel
import org.flixelgdx.debug.ui.FlixelDebugPanels
import org.flixelgdx.debug.ui.FlixelDebugProgress
import org.flixelgdx.debug.ui.FlixelDebugProperties
import org.flixelgdx.debug.ui.FlixelDebugScroll
import org.flixelgdx.debug.ui.FlixelDebugSeparator
import org.flixelgdx.debug.ui.FlixelDebugSlider
import org.flixelgdx.debug.ui.FlixelDebugSplit
import org.flixelgdx.debug.ui.FlixelDebugStack
import org.flixelgdx.debug.ui.FlixelDebugTable
import org.flixelgdx.debug.ui.FlixelDebugTabs
import org.flixelgdx.debug.ui.FlixelDebugTextBox
import org.flixelgdx.debug.ui.FlixelDebugToggle
import org.flixelgdx.debug.ui.FlixelDebugTree
import org.flixelgdx.debug.ui.FlixelDebugUi
import org.flixelgdx.debug.ui.FlixelDebugWidget
import org.flixelgdx.debug.ui.FlixelDebugWrap
import org.flixelgdx.functional.consumer.FloatConsumer
import org.flixelgdx.util.FlixelString

/*
 * A builder DSL for debugger panels.
 *
 * In Java, a panel is written as nested factory calls such as `column(row(button(...)))`, and
 * anything that changes every frame needs a subclass that overrides `update(...)`. This DSL
 * writes the same tree as nested blocks, and `onUpdate { }` replaces the subclass:
 *
 * ```kotlin
 * Flixel.debug.panels.panel("game.enemies", "Enemies", dock = FlixelDebugDock.RIGHT) {
 *   heading("Enemies")
 *   label { out -> out.concat(enemies.countLiving()).concat(" alive") }
 *   checkbox("Frozen", enemies::isFrozen, enemies::setFrozen)
 *   slider(0.0..2.0, { enemies.speedScale.toDouble() }) { enemies.speedScale = it.toFloat() }
 *   val wave = progress()
 *   row {
 *     button("Spawn wave") { enemies.spawnWave() }
 *     button("Kill all", confirm = "Kill every enemy?", tone = FlixelDebugTone.ERROR) {
 *       enemies.killAll()
 *     }
 *   }
 *   onUpdate { wave.setValue(enemies.waveProgress) }
 * }
 * ```
 *
 * Every builder is `inline`, and getters, setters, and click handlers are `crossinline`, so each
 * becomes exactly one listener object while the panel is built and nothing is allocated per
 * frame. Getters still run every frame while the panel is open, so keep them allocation free.
 */

/**
 * Marks the receivers of the debugger panel DSL so a block can only add to its own widget.
 *
 * Without it, calling `label(...)` inside a `table { }` block would quietly add the label to the
 * container around the table. With the marker, the compiler rejects the call instead. Reach an
 * outer receiver on purpose with a labeled `this`, such as `this@column`.
 */
@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE, AnnotationTarget.TYPEALIAS)
annotation class FlixelDebugDsl

/**
 * A debugger panel that can run a callback every frame, so it does not need its own subclass.
 *
 * [FlixelDebugPanels.panel] creates one. Extend it instead of [FlixelDebugPanel] when you build
 * your own panel class with [content] and still want [onUpdate].
 *
 * @param id The stable id, for example `"game.enemies"`. Keep it the same between versions.
 * @param title The name on the panel's tab.
 */
open class FlixelDebugDslPanel(id: String, title: String) : FlixelDebugPanel(id, title) {

  private var updateListener: FloatConsumer? = null

  /**
   * Sets the callback that runs every frame while the panel is open, after the widgets built with
   * getters have refreshed. Replaces any earlier callback.
   *
   * @param listener Runs with the frame's elapsed seconds, or `null` to remove the callback.
   */
  fun onUpdate(listener: FloatConsumer?) {
    updateListener = listener
  }

  override fun update(elapsed: Float) {
    super.update(elapsed)
    updateListener?.accept(elapsed)
  }
}

/**
 * Builds a panel with [block] and registers it, so it shows up in the debugger app.
 *
 * The block's receiver is the panel's root column.
 *
 * @param id The stable id, for example `"game.enemies"`. Pick ids that do not clash with the
 *   built-in panels, such as ones starting with `"game."`.
 * @param title The name on the panel's tab.
 * @param dock Where the panel opens.
 * @param updatedWhileClosed Whether the panel keeps updating while its tab is closed, for example
 *   to collect graph history.
 * @param replace Whether to replace a registered panel with the same id, such as a built-in one,
 *   instead of failing.
 * @param block Builds the panel's widgets.
 * @return The registered panel.
 * @throws IllegalArgumentException If a panel with the same id exists and [replace] is `false`.
 */
inline fun FlixelDebugPanels.panel(
  id: String,
  title: String,
  dock: FlixelDebugDock = FlixelDebugDock.CENTER,
  updatedWhileClosed: Boolean = false,
  replace: Boolean = false,
  block: @FlixelDebugDsl FlixelDebugStack.() -> Unit,
): FlixelDebugDslPanel {
  val panel = FlixelDebugDslPanel(id, title)
  panel.setDock(dock)
  panel.setUpdatedWhileClosed(updatedWhileClosed)
  panel.content(block)
  if (replace) replace(panel) else register(panel)
  return panel
}

/**
 * Replaces this panel's content with a column built by [block].
 *
 * Use it in your own panel classes:
 * ```kotlin
 * class EnemyPanel(enemies: EnemyGroup) : FlixelDebugDslPanel("game.enemies", "Enemies") {
 *   init {
 *     content { heading("Enemies"); button("Kill all") { enemies.killAll() } }
 *   }
 * }
 * ```
 *
 * @param block Builds the widgets.
 * @return The new root column.
 */
inline fun FlixelDebugPanel.content(
  block: @FlixelDebugDsl FlixelDebugStack.() -> Unit
): FlixelDebugStack {
  val root = FlixelDebugStack(false)
  setContent(root)
  root.block()
  return root
}

/**
 * Runs [listener] every frame while this widget's panel is open.
 *
 * Use it to push values into widgets that have no getter, such as a progress bar or a table. Each
 * panel has one callback, so calling this again replaces the earlier one.
 *
 * @param listener Runs with the frame's elapsed seconds.
 * @throws IllegalStateException If the panel is not a [FlixelDebugDslPanel].
 */
fun FlixelDebugContainer<*>.onUpdate(listener: FloatConsumer) {
  val owner =
    panel as? FlixelDebugDslPanel
      ?: error("onUpdate needs a panel built with panels.panel(...) or a FlixelDebugDslPanel.")
  owner.onUpdate(listener)
}

/**
 * Adds [widget] as the last child of this container and returns it.
 *
 * Public only so the inline builders can call it; it is not part of the API.
 */
@PublishedApi
internal fun <W : FlixelDebugWidget<*>> FlixelDebugContainer<*>.addChild(widget: W): W {
  insert(childCount, widget)
  return widget
}

/**
 * Adds a column, whose children stack from top to bottom.
 *
 * @param spacing The gap between children, in pixels.
 * @param block Adds the column's children.
 * @return The new column.
 */
inline fun FlixelDebugContainer<*>.column(
  spacing: Float = FlixelDebugStack.DEFAULT_SPACING,
  block: @FlixelDebugDsl FlixelDebugStack.() -> Unit,
): FlixelDebugStack = addChild(FlixelDebugStack(false).setSpacing(spacing)).also { it.block() }

/**
 * Adds a row, whose children sit side by side.
 *
 * @param spacing The gap between children, in pixels.
 * @param block Adds the row's children.
 * @return The new row.
 */
inline fun FlixelDebugContainer<*>.row(
  spacing: Float = FlixelDebugStack.DEFAULT_SPACING,
  block: @FlixelDebugDsl FlixelDebugStack.() -> Unit,
): FlixelDebugStack = addChild(FlixelDebugStack(true).setSpacing(spacing)).also { it.block() }

/**
 * Adds a row that wraps onto new lines when it runs out of room.
 *
 * @param block Adds the children.
 * @return The new wrapping row.
 */
inline fun FlixelDebugContainer<*>.wrap(
  block: @FlixelDebugDsl FlixelDebugWrap.() -> Unit
): FlixelDebugWrap = addChild(FlixelDebugWrap()).also { it.block() }

/**
 * Adds a grid that fills its cells one row after another.
 *
 * @param columns The size of each column, as [FlixelDebugGrid] accepts them.
 * @param block Adds the cells.
 * @return The new grid.
 */
inline fun FlixelDebugContainer<*>.grid(
  vararg columns: String,
  block: @FlixelDebugDsl FlixelDebugGrid.() -> Unit,
): FlixelDebugGrid = addChild(FlixelDebugGrid(*columns)).also { it.block() }

/**
 * Adds a scrolling area around a column built by [block].
 *
 * @param block Adds the scrolling content.
 * @return The new scroll area.
 */
inline fun FlixelDebugContainer<*>.scroll(
  block: @FlixelDebugDsl FlixelDebugStack.() -> Unit
): FlixelDebugScroll {
  val inner = FlixelDebugStack(false)
  val scroll = addChild(FlixelDebugScroll(inner))
  inner.block()
  return scroll
}

/**
 * Adds a header that folds a column built by [block] open and closed.
 *
 * @param header The header's text.
 * @param expanded Whether it starts open.
 * @param block Adds the folding content.
 * @return The new expander.
 */
inline fun FlixelDebugContainer<*>.expander(
  header: CharSequence,
  expanded: Boolean = false,
  block: @FlixelDebugDsl FlixelDebugStack.() -> Unit,
): FlixelDebugExpander {
  val inner = FlixelDebugStack(false)
  val expander = addChild(FlixelDebugExpander(header, inner).setExpanded(expanded))
  inner.block()
  return expander
}

/**
 * Adds two areas with a handle between them. Add exactly two children in [block].
 *
 * @param horizontal Whether the areas sit side by side instead of one above the other.
 * @param block Adds the two areas, usually two [column]s.
 * @return The new split.
 */
inline fun FlixelDebugContainer<*>.split(
  horizontal: Boolean = true,
  block: @FlixelDebugDsl FlixelDebugSplit.() -> Unit,
): FlixelDebugSplit = addChild(FlixelDebugSplit(horizontal)).also { it.block() }

/**
 * Adds pages behind tabs. Add them with [page].
 *
 * @param block Adds the pages.
 * @return The new tabs.
 */
inline fun FlixelDebugContainer<*>.tabs(
  block: @FlixelDebugDsl FlixelDebugTabs.() -> Unit
): FlixelDebugTabs = addChild(FlixelDebugTabs()).also { it.block() }

/**
 * Adds a page whose content is a column built by [block].
 *
 * @param title The tab's title.
 * @param block Adds the page's widgets.
 * @return The page's column.
 */
inline fun FlixelDebugTabs.page(
  title: String,
  block: @FlixelDebugDsl FlixelDebugStack.() -> Unit,
): FlixelDebugStack {
  val page = FlixelDebugStack(false)
  addPage(title, page)
  page.block()
  return page
}

/**
 * Adds a fixed line of text.
 *
 * @param text The text to show.
 * @return The new label.
 */
fun FlixelDebugContainer<*>.label(text: CharSequence): FlixelDebugLabel =
  addChild(FlixelDebugUi.label(text))

/**
 * Adds a line of text that is rebuilt every frame while the panel is open.
 *
 * @param appender Appends the current text to a reused buffer, so nothing is allocated.
 * @return The new label.
 */
inline fun FlixelDebugContainer<*>.label(
  crossinline appender: (FlixelString) -> Unit
): FlixelDebugLabel = addChild(FlixelDebugUi.label { appender(it) })

/**
 * Adds a heading.
 *
 * @param text The heading's text.
 * @return The new label.
 */
fun FlixelDebugContainer<*>.heading(text: CharSequence): FlixelDebugLabel =
  addChild(FlixelDebugUi.heading(text))

/**
 * Adds a dim, wrapping line of help text.
 *
 * @param text The hint's text.
 * @return The new label.
 */
fun FlixelDebugContainer<*>.hint(text: CharSequence): FlixelDebugLabel =
  addChild(FlixelDebugUi.hint(text))

/**
 * Adds a thin dividing line.
 *
 * @return The new separator.
 */
fun FlixelDebugContainer<*>.separator(): FlixelDebugSeparator = addChild(FlixelDebugUi.separator())

/**
 * Adds a button.
 *
 * @param text The button's text.
 * @param confirm A question the app asks before running [onClick], or `null` to run it right away.
 * @param tone The button's color, for example [FlixelDebugTone.ERROR] for destructive actions.
 * @param onClick Runs when the button is clicked.
 * @return The new button.
 */
inline fun FlixelDebugContainer<*>.button(
  text: CharSequence,
  confirm: String? = null,
  tone: FlixelDebugTone = FlixelDebugTone.NORMAL,
  crossinline onClick: () -> Unit,
): FlixelDebugButton {
  val button = addChild(FlixelDebugUi.button(text) { onClick() }.setConfirm(confirm))
  button.tone = tone
  return button
}

/**
 * Adds a checkbox that shows [get] and calls [set] when the person changes it.
 *
 * @param text The checkbox's label.
 * @param get Reads the current value every frame.
 * @param set Applies a new value.
 * @return The new checkbox.
 */
inline fun FlixelDebugContainer<*>.checkbox(
  text: CharSequence,
  crossinline get: () -> Boolean,
  crossinline set: (Boolean) -> Unit,
): FlixelDebugCheckbox = addChild(FlixelDebugUi.checkbox(text, { get() }, { set(it) }))

/**
 * Adds a switch that shows [get] and calls [set] when the person flips it.
 *
 * @param text The switch's label.
 * @param get Reads the current value every frame.
 * @param set Applies a new value.
 * @return The new switch.
 */
inline fun FlixelDebugContainer<*>.toggle(
  text: CharSequence,
  crossinline get: () -> Boolean,
  crossinline set: (Boolean) -> Unit,
): FlixelDebugToggle = addChild(FlixelDebugUi.toggle(text, { get() }, { set(it) }))

/**
 * Adds a slider over [range] that shows [get] and calls [set] when the person drags it.
 *
 * @param range The lowest and highest values.
 * @param get Reads the current value every frame.
 * @param set Applies a new value.
 * @return The new slider.
 */
inline fun FlixelDebugContainer<*>.slider(
  range: ClosedFloatingPointRange<Double>,
  crossinline get: () -> Double,
  crossinline set: (Double) -> Unit,
): FlixelDebugSlider =
  addChild(FlixelDebugUi.slider(range.start, range.endInclusive, { get() }, { set(it) }))

/**
 * Adds a number field that shows [get] and calls [set] when the person edits it.
 *
 * @param get Reads the current value every frame.
 * @param set Applies a new value.
 * @return The new number field.
 */
inline fun FlixelDebugContainer<*>.number(
  crossinline get: () -> Double,
  crossinline set: (Double) -> Unit,
): FlixelDebugNumber = addChild(FlixelDebugUi.number({ get() }, { set(it) }))

/**
 * Adds a progress bar whose value you set yourself, for example in [onUpdate].
 *
 * @return The new progress bar.
 */
fun FlixelDebugContainer<*>.progress(): FlixelDebugProgress = addChild(FlixelDebugProgress())

/**
 * Adds a progress bar that reads its value from [get] every frame.
 *
 * @param get Reads the progress, from `0` to `1`.
 * @return The new progress bar.
 */
inline fun FlixelDebugContainer<*>.progress(crossinline get: () -> Double): FlixelDebugProgress =
  addChild(FlixelDebugProgress { get() })

/**
 * Adds a text box.
 *
 * @param placeholder Gray text shown while the box is empty, or `null` for none.
 * @return The new text box.
 */
fun FlixelDebugContainer<*>.textBox(placeholder: String? = null): FlixelDebugTextBox =
  addChild(FlixelDebugUi.textBox(placeholder))

/**
 * Adds a text box that calls [onSubmit] when the person presses Enter.
 *
 * @param placeholder Gray text shown while the box is empty, or `null` for none.
 * @param clearOnSubmit Whether the box empties itself after a submit.
 * @param onSubmit Runs with the submitted text.
 * @return The new text box.
 */
inline fun FlixelDebugContainer<*>.textBox(
  placeholder: String?,
  clearOnSubmit: Boolean = false,
  crossinline onSubmit: (CharSequence) -> Unit,
): FlixelDebugTextBox =
  addChild(
    FlixelDebugUi.textBox(placeholder).setClearOnSubmit(clearOnSubmit).onSubmit { onSubmit(it) }
  )

/**
 * Adds a dropdown of [items] that shows the index from [get] and calls [set] when the person picks
 * another one.
 *
 * @param items The choices, in order.
 * @param get Reads the selected index every frame.
 * @param set Applies a newly picked index.
 * @return The new dropdown.
 */
inline fun FlixelDebugContainer<*>.dropdown(
  vararg items: CharSequence,
  crossinline get: () -> Int,
  crossinline set: (Int) -> Unit,
): FlixelDebugDropdown = addChild(FlixelDebugDropdown({ get() }, { set(it) }).setItems(*items))

/**
 * Adds a color picker that shows the ARGB color from [get] and calls [set] when the person picks
 * another one.
 *
 * @param get Reads the current color, as `0xAARRGGBB`, every frame.
 * @param set Applies a newly picked color.
 * @return The new color picker.
 */
inline fun FlixelDebugContainer<*>.colorPicker(
  crossinline get: () -> Int,
  crossinline set: (Int) -> Unit,
): FlixelDebugColorPicker = addChild(FlixelDebugColorPicker({ get() }, { set(it) }))

/**
 * Adds a table. Set its columns in [block], and fill its rows later, for example in [onUpdate].
 *
 * @param block Configures the table.
 * @return The new table.
 */
inline fun FlixelDebugContainer<*>.table(
  block: @FlixelDebugDsl FlixelDebugTable.() -> Unit = {}
): FlixelDebugTable = addChild(FlixelDebugTable()).also { it.block() }

/**
 * Adds a tree. Fill its nodes later, for example in [onUpdate].
 *
 * @param block Configures the tree.
 * @return The new tree.
 */
inline fun FlixelDebugContainer<*>.tree(
  block: @FlixelDebugDsl FlixelDebugTree.() -> Unit = {}
): FlixelDebugTree = addChild(FlixelDebugTree()).also { it.block() }

/**
 * Adds a graph of numbers over time. Add its series in [block] and push samples later.
 *
 * @param title The graph's title.
 * @param block Configures the graph, for example with `series(...)`.
 * @return The new graph.
 */
inline fun FlixelDebugContainer<*>.graph(
  title: CharSequence,
  block: @FlixelDebugDsl FlixelDebugGraph.() -> Unit = {},
): FlixelDebugGraph = addChild(FlixelDebugGraph(title)).also { it.block() }

/**
 * Adds a canvas of boxes that can be picked, dragged, panned, and zoomed.
 *
 * @param block Configures the canvas.
 * @return The new canvas.
 */
inline fun FlixelDebugContainer<*>.canvas(
  block: @FlixelDebugDsl FlixelDebugCanvas.() -> Unit = {}
): FlixelDebugCanvas = addChild(FlixelDebugCanvas()).also { it.block() }

/**
 * Adds an image that shows PNG data you set on it.
 *
 * @return The new image.
 */
fun FlixelDebugContainer<*>.image(): FlixelDebugImage = addChild(FlixelDebugImage())

/**
 * Adds a property grid of named, editable values in foldable sections.
 *
 * @param block Adds the grid's sections.
 * @return The new property grid.
 */
inline fun FlixelDebugContainer<*>.properties(
  block: @FlixelDebugDsl FlixelDebugProperties.() -> Unit
): FlixelDebugProperties = addChild(FlixelDebugProperties()).also { it.block() }
