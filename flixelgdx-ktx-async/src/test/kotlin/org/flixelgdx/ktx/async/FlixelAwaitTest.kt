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
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.launch
import org.flixelgdx.FlixelGame
import org.flixelgdx.animation.FlixelAnimationController
import org.flixelgdx.graphics.FlixelBatch
import org.flixelgdx.tween.FlixelTween
import org.flixelgdx.util.signal.FlixelSignal

/**
 * Tests for [FlixelSignal.awaitOnce] continuation wiring.
 *
 * <p>NOTE: Tests for [FlixelTween.await] and [FlixelAnimationController.await] require a running
 * tween manager and a sprite with animations respectively. Both depend on the full game loop (
 * [FlixelBatch], [FlixelGame], etc.) and cannot be driven headlessly. For those, use an integration
 * test project. The signal-based [awaitOnce] test is fully headless because [FlixelSignal] has no
 * framework dependencies.
 */
class FlixelAwaitTest {

  @Test
  fun awaitOnceResumesWithDispatchedValue() {
    val signal = FlixelSignal<String>()
    var received: String? = null

    // Launch a coroutine that waits for the signal.
    scope.launch(Dispatcher) { received = signal.awaitOnce() }

    // Pump so the coroutine starts and parks at awaitOnce.
    Dispatcher.update()
    assertNull(received, "Value should not be set before the signal fires")

    // Dispatch the signal - this resumes the continuation and enqueues the next block.
    signal.dispatch("hello")

    // Pump again to run the resumed block.
    Dispatcher.update()
    assertEquals("hello", received, "Coroutine should have resumed with the dispatched value")
  }

  @Test
  fun awaitOnceHandlerIsRemovedAfterFire() {
    val signal = FlixelSignal<Int>()
    val results = mutableListOf<Int>()

    scope.launch(Dispatcher) { results.add(signal.awaitOnce()) }

    Dispatcher.update()

    // First dispatch - should resume the coroutine.
    signal.dispatch(10)
    Dispatcher.update()

    // Second dispatch - the one-time handler should be gone.
    signal.dispatch(20)
    Dispatcher.update()

    assertEquals(listOf(10), results, "awaitOnce should only capture the first dispatch")
  }

  @Test
  fun awaitOnceWorksWithNullableVoidSignal() {
    val signal = FlixelSignal<Void>()
    var resumed = false

    scope.launch(Dispatcher) {
      signal.awaitOnce()
      resumed = true
    }

    Dispatcher.update()
    assertTrue(!resumed)

    signal.dispatch()
    Dispatcher.update()

    assertTrue(resumed, "Coroutine should resume when a Void signal fires via dispatch()")
  }

  @Test
  fun multipleConcurrentAwaitOnceSuspensions() {
    val signal = FlixelSignal<Int>()
    val results = mutableListOf<Int>()

    // Launch two coroutines both awaiting the same signal.
    scope.launch(Dispatcher) { results.add(signal.awaitOnce()) }
    scope.launch(Dispatcher) { results.add(signal.awaitOnce()) }

    // Pump once to start both coroutines.
    Dispatcher.update()
    assertTrue(results.isEmpty())

    // A single dispatch resumes both one-time handlers.
    signal.dispatch(42)
    Dispatcher.update()

    assertEquals(2, results.size, "Both coroutines should have been resumed by the single dispatch")
    assertTrue(results.all { it == 42 }, "Both should receive the same dispatched value")
  }
}
