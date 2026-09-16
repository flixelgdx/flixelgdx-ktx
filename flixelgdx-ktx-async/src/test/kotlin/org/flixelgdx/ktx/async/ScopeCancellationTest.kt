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
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.flixelgdx.Flixel
import org.flixelgdx.util.signal.FlixelSignal
import org.flixelgdx.util.signal.FlixelSignalData.StateSwitchSignalData

/**
 * Tests that scope children are cancelled when [Flixel.Signals.preStateSwitch] fires, and that the
 * scope itself stays alive for reuse.
 *
 * NOTE: [installFlixelCoroutines] subscribes to [Flixel.Signals.preStateSwitch], a static
 * global. To keep this test isolated from global state, it drives scope cancellation directly via
 * [cancelChildren], which is the same call [installFlixelCoroutines] makes. This avoids spinning up
 * [Flixel] in a headless environment while still verifying the wiring logic.
 */
class ScopeCancellationTest {

  @Test
  fun childJobIsCancelledWhenScopeCancelsChildren() {
    val signal = FlixelSignal<String>()
    var job: Job? = null

    // Launch a coroutine that waits indefinitely on a signal that will never fire.
    job = scope.launch(Dispatcher) { signal.awaitOnce() }

    Dispatcher.update()
    assertTrue(job!!.isActive, "Job should be active while the signal has not fired")

    // Simulate what installFlixelCoroutines does when preStateSwitch fires.
    scope.coroutineContext.cancelChildren()

    // The job should now be cancelled.
    assertFalse(job!!.isActive, "Job should be cancelled after cancelChildren()")
    assertTrue(job!!.isCancelled, "Job.isCancelled should be true")
  }

  @Test
  fun scopeRemainsActiveAfterCancelChildren() {
    scope.coroutineContext.cancelChildren()

    assertTrue(
      scope.isActive,
      "scope itself should remain active so new coroutines can be launched after a state switch",
    )
  }

  @Test
  fun newCoroutineLaunchedAfterCancelChildrenRuns() {
    scope.coroutineContext.cancelChildren()

    var ran = false
    scope.launch(Dispatcher) { ran = true }

    Dispatcher.update()
    assertTrue(ran, "A new coroutine launched after cancelChildren() should run normally")
  }

  @Test
  fun stateSwitchSignalCarriesNewStateData() {
    // Verify that the StateSwitchSignalData record is constructable and readable in isolation.
    // installFlixelCoroutines uses the signal handler parameter but only calls cancelChildren.
    val signal = FlixelSignal<StateSwitchSignalData>()
    var receivedState: StateSwitchSignalData? = null

    signal.addOnce { data -> receivedState = data }

    val mockData = StateSwitchSignalData(null)
    signal.dispatch(mockData)

    assertTrue(
      receivedState === mockData,
      "Signal should carry the dispatched StateSwitchSignalData",
    )
  }

  @Test
  fun cancelledChildDoesNotPreventNewLaunch() {
    var firstRan = false
    var secondRan = false

    val job =
      scope.launch(Dispatcher) {
        val signal = FlixelSignal<Unit>()
        signal.awaitOnce() // parks forever
        firstRan = true
      }

    Dispatcher.update()
    scope.coroutineContext.cancelChildren()

    // firstRan must stay false because the coroutine was cancelled before the signal ever fired.
    Dispatcher.update()
    assertFalse(firstRan, "Cancelled coroutine must not execute post-suspension code")

    scope.launch(Dispatcher) { secondRan = true }

    Dispatcher.update()
    assertTrue(secondRan, "New coroutine after cancel should execute normally")
  }
}
