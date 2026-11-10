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
@file:JvmName("FlixelDebugStyles")

package org.flixelgdx.ktx.debug

import org.flixelgdx.debug.remote.FlixelDebugProtocol
import org.flixelgdx.debug.ui.FlixelDebugPanel
import org.flixelgdx.debug.ui.FlixelDebugPanels
import org.flixelgdx.debug.ui.FlixelDebugWidget

/**
 * A color tone for a debugger widget, table row, tree badge, or toast.
 *
 * The debugger protocol stores tones as plain `Int` values, the same type as docks and style flags,
 * so passing the wrong one compiles without complaint. This value class gives tones their own type,
 * so the compiler catches the mix-up, while still compiling down to a plain `Int`.
 *
 * @property code The protocol's `TONE_*` value.
 */
@JvmInline
value class FlixelDebugTone(val code: Int) {

  /** The tones the debugger app knows how to draw. */
  companion object {
    /** The default text color. */
    val NORMAL = FlixelDebugTone(FlixelDebugProtocol.TONE_NORMAL)

    /** A dimmer color for secondary information. */
    val MUTED = FlixelDebugTone(FlixelDebugProtocol.TONE_MUTED)

    /** The app's highlight color. */
    val ACCENT = FlixelDebugTone(FlixelDebugProtocol.TONE_ACCENT)

    /** A color that means something went well. */
    val SUCCESS = FlixelDebugTone(FlixelDebugProtocol.TONE_SUCCESS)

    /** A color that asks for attention. */
    val WARNING = FlixelDebugTone(FlixelDebugProtocol.TONE_WARNING)

    /** A color for errors and destructive actions. */
    val ERROR = FlixelDebugTone(FlixelDebugProtocol.TONE_ERROR)
  }
}

/**
 * The side of the debugger app a panel opens on.
 *
 * Like [FlixelDebugTone], this gives the protocol's `DOCK_*` integers a type of their own.
 *
 * @property code The protocol's `DOCK_*` value.
 */
@JvmInline
value class FlixelDebugDock(val code: Int) {

  /** The dock positions the debugger app supports. */
  companion object {
    /** The main area in the middle of the app. */
    val CENTER = FlixelDebugDock(FlixelDebugProtocol.DOCK_CENTER)

    /** The column on the left side. */
    val LEFT = FlixelDebugDock(FlixelDebugProtocol.DOCK_LEFT)

    /** The column on the right side. */
    val RIGHT = FlixelDebugDock(FlixelDebugProtocol.DOCK_RIGHT)

    /** The strip along the bottom. */
    val BOTTOM = FlixelDebugDock(FlixelDebugProtocol.DOCK_BOTTOM)
  }
}

/**
 * The tone of this widget.
 *
 * A widget's style packs the tone together with flags such as bold or monospace. Setting this
 * property only replaces the tone and keeps every other flag as it was.
 */
var FlixelDebugWidget<*>.tone: FlixelDebugTone
  get() = FlixelDebugTone(style and FlixelDebugProtocol.STYLE_TONE_MASK)
  set(value) {
    setStyle((style and FlixelDebugProtocol.STYLE_TONE_MASK.inv()) or value.code)
  }

/**
 * Sets the side of the app this panel opens on.
 *
 * @param dock Where the panel opens.
 * @return This panel, for chaining.
 */
fun FlixelDebugPanel.setDock(dock: FlixelDebugDock): FlixelDebugPanel = setDock(dock.code)

/**
 * Asks the app to show a short message.
 *
 * @param tone The color of the message.
 * @param text The message to show.
 */
fun FlixelDebugPanels.toast(tone: FlixelDebugTone, text: String) = toast(tone.code, text)
