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
package org.flixelgdx.ktx.async

import kotlin.coroutines.resume
import org.flixelgdx.debug.feeds.FlixelDebugInspector

/**
 * Suspends until an object of type [T] is selected in the debugger's inspector, then returns it.
 *
 * Useful for debug tools that need a target, such as a console command that waits for you to pick
 * an enemy:
 * ```kotlin
 * Flixel.debug.console.register("kill") {
 *   launch {
 *     Flixel.info("Pick an enemy to kill.")
 *     Flixel.debug.inspector.awaitSelectionOf<Enemy>().kill()
 *   }
 * }
 * ```
 *
 * Selections of other types, and clearing the selection, are ignored. The selection listener is
 * removed after the coroutine resumes or is cancelled.
 *
 * @param T The type of object to wait for.
 * @return The selected object.
 */
suspend inline fun <reified T : Any> FlixelDebugInspector.awaitSelectionOf(): T {
  var listener: FlixelDebugInspector.SelectionListener? = null
  try {
    return suspendCancellable { cont ->
      val waiter =
        FlixelDebugInspector.SelectionListener { selected ->
          if (selected is T && cont.isActive) {
            cont.resume(selected)
          }
        }
      listener = waiter
      addSelectionListener(waiter)
    }
  } finally {
    // The inspector walks its listener list while it notifies, so removing a listener from inside
    // the callback would skip the next one. The coroutine resumes on the dispatcher, after that
    // walk has finished, so removing it here is safe.
    listener?.let { removeSelectionListener(it) }
  }
}

/**
 * Suspends until any object is selected in the debugger's inspector, then returns it.
 *
 * Clearing the selection is ignored.
 *
 * @return The selected object.
 * @see awaitSelectionOf
 */
suspend fun FlixelDebugInspector.awaitSelection(): Any = awaitSelectionOf<Any>()
