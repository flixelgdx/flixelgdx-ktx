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

import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertSame
import org.flixelgdx.debug.FlixelDebugObjectIds
import org.flixelgdx.debug.feeds.FlixelDebugInspector

class FlixelDebugAwaitTest {

  private class Enemy

  private class Coin

  private val inspector = FlixelDebugInspector(FlixelDebugObjectIds())

  @Test
  fun awaitSelectionOfSkipsOtherTypes() {
    var picked: Enemy? = null
    launch { picked = inspector.awaitSelectionOf<Enemy>() }
    Dispatcher.update()

    inspector.select(Coin())
    Dispatcher.update()
    assertNull(picked)

    val enemy = Enemy()
    inspector.select(enemy)
    Dispatcher.update()
    assertSame(enemy, picked)
  }

  @Test
  fun awaitSelectionRemovesItsListenerWithoutSkippingOthers() {
    var first: Any? = null
    var other: Any? = null
    launch { first = inspector.awaitSelection() }
    Dispatcher.update()
    inspector.addSelectionListener { other = it }

    val coin = Coin()
    inspector.select(coin)
    Dispatcher.update()
    assertSame(coin, first)
    assertSame(coin, other, "the listener after the waiter must still be called")

    // Once resumed, the waiter is gone and later selections only reach the other listener.
    val next = Enemy()
    inspector.select(next)
    Dispatcher.update()
    assertSame(coin, first)
    assertSame(next, other)
  }

  @Test
  fun cancellingStopsWaiting() {
    var picked: Any? = null
    val job = launch { picked = inspector.awaitSelection() }
    Dispatcher.update()

    job.cancel()
    Dispatcher.update()
    inspector.select(Coin())
    Dispatcher.update()

    assertNull(picked)
  }
}
