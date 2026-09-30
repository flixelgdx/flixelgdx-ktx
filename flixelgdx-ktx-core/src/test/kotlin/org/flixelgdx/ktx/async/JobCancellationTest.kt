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

import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume
import kotlin.coroutines.startCoroutine
import kotlin.coroutines.suspendCoroutine
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.flixelgdx.Flixel
import org.flixelgdx.signal.FlixelSignal

/**
 * Tests for [FlixelJob] state, [cancelAllCoroutines], and how cancellation reaches a coroutine.
 *
 * NOTE: [installFlixelCoroutines] subscribes to [Flixel.Signals.preStateSwitch], a static global.
 * To keep these tests isolated from global state, they call [cancelAllCoroutines] directly, which
 * is the same call [installFlixelCoroutines] makes on a state switch.
 */
class JobCancellationTest {

  @Test
  fun jobIsCancelledByCancelAllCoroutines() {
    val signal = FlixelSignal<String>()
    val job = launch { signal.awaitOnce() }

    Dispatcher.update()
    assertTrue(job.isActive, "Job should be active while the signal has not fired")

    cancelAllCoroutines()

    assertFalse(job.isActive, "Job should not be active after cancelAllCoroutines()")
    assertTrue(job.isCancelled, "Job.isCancelled should be true")
  }

  @Test
  fun newCoroutineLaunchedAfterCancelAllRuns() {
    cancelAllCoroutines()

    var ran = false
    launch { ran = true }

    Dispatcher.update()
    assertTrue(ran, "A coroutine launched after cancelAllCoroutines() should run normally")
  }

  @Test
  fun cancelledCoroutineDoesNotRunPostSuspensionCode() {
    var firstRan = false
    var secondRan = false

    launch {
      FlixelSignal<Unit>().awaitOnce()
      firstRan = true
    }

    Dispatcher.update()
    cancelAllCoroutines()
    Dispatcher.update()
    assertFalse(firstRan, "Cancelled coroutine must not execute post-suspension code")

    launch { secondRan = true }

    Dispatcher.update()
    assertTrue(secondRan, "New coroutine after cancel should execute normally")
  }

  @Test
  fun jobCompletesAfterBodyReturns() {
    val job = launch {}

    assertFalse(job.isCompleted, "The body starts on the next dispatcher pass, not in launch")
    Dispatcher.update()

    assertTrue(job.isCompleted)
    assertFalse(job.isCancelled)
    assertFalse(job.isActive)
  }

  @Test
  fun cancelBeforeStartSkipsTheBody() {
    var ran = false
    val job = launch { ran = true }

    job.cancel()
    Dispatcher.update()

    assertFalse(ran, "A coroutine canceled before its first pass must not run its body")
    assertTrue(job.isCompleted)
    assertTrue(job.isCancelled)
  }

  @Test
  fun finallyBlockRunsOnCancellation() {
    var cleanedUp = false
    var caught: Throwable? = null
    val job = launch {
      try {
        FlixelSignal<Unit>().awaitOnce()
      } catch (e: CancellationException) {
        caught = e
        throw e
      } finally {
        cleanedUp = true
      }
    }

    Dispatcher.update()
    job.cancel()
    assertFalse(cleanedUp, "The coroutine resumes with the cancellation on the next pass")

    Dispatcher.update()
    assertTrue(cleanedUp, "finally blocks must run when a coroutine is canceled")
    assertTrue(caught is CancellationException)
    assertTrue(job.isCompleted)
  }

  @Test
  fun cancellationHandlerRunsImmediately() {
    var handled = false
    val job = launch {
      suspendCancellable<Unit> { cont -> cont.invokeOnCancellation { handled = true } }
    }

    Dispatcher.update()
    job.cancel()

    assertTrue(handled, "Cleanup must run at the moment the job is canceled")
  }

  @Test
  fun cancelledJobThrowsAtNonCancellableSuspension() {
    var captured: Continuation<Unit>? = null
    var afterResume = false
    val job = launch {
      suspendCoroutine<Unit> { captured = it }
      afterResume = true
    }

    Dispatcher.update()
    job.cancel()
    captured!!.resume(Unit)
    Dispatcher.update()

    assertFalse(afterResume, "A canceled job must not continue after a plain suspension resumes")
    assertTrue(job.isCompleted)
  }

  @Test
  fun receiverIsTheCoroutinesOwnJob() {
    var receiver: FlixelJob? = null
    var activeInside = false
    val job = launch {
      receiver = this
      activeInside = isActive
    }

    Dispatcher.update()

    assertTrue(receiver === job)
    assertTrue(activeInside)
  }

  @Test
  fun selfCancelStopsAtNextSuspension() {
    var afterSuspension = false
    val job = launch {
      cancel()
      FlixelSignal<Unit>().awaitOnce()
      afterSuspension = true
    }

    Dispatcher.update()
    Dispatcher.update()

    assertFalse(afterSuspension)
    assertTrue(job.isCompleted)
    assertTrue(job.isCancelled)
  }

  @Test
  fun exceptionPropagatesOutOfDispatcherUpdate() {
    launch { throw IllegalStateException("boom") }

    val error = assertFailsWith<IllegalStateException> { Dispatcher.update() }
    assertEquals("boom", error.message)
  }

  @Test
  fun resumeDuringSetupReturnsImmediately() {
    var value: Int? = null
    var passes = 0
    launch {
      value = suspendCancellable { cont -> cont.resume(7) }
      passes++
    }

    Dispatcher.update()

    assertEquals(7, value)
    assertEquals(1, passes, "A resume during setup continues without waiting for another pass")
  }

  @Test
  fun resumeDuringSetupWorksWithoutTheDispatcher() {
    var value: String? = null
    val body: suspend () -> Unit = { value = suspendCancellable { cont -> cont.resume("sync") } }

    body.startCoroutine(
      object : Continuation<Unit> {
        override val context = EmptyCoroutineContext

        override fun resumeWith(result: Result<Unit>) = result.getOrThrow()
      }
    )

    assertEquals("sync", value)
  }

  @Test
  fun secondResumeIsIgnored() {
    var cont: FlixelCancellableContinuation<Int>? = null
    val results = mutableListOf<Int>()
    launch { results.add(suspendCancellable { cont = it }) }

    Dispatcher.update()
    cont!!.resume(1)
    cont!!.resume(2)
    Dispatcher.update()

    assertEquals(listOf(1), results)
    assertNull(cont!!.takeIf { it.isActive })
  }

  @Test
  fun coroutineLaunchedDuringCancellationSurvives() {
    var late: FlixelJob? = null
    launch {
      suspendCancellable<Unit> { cont ->
        cont.invokeOnCancellation { late = launch { FlixelSignal<Unit>().awaitOnce() } }
      }
    }

    Dispatcher.update()
    cancelAllCoroutines()

    assertTrue(late!!.isActive, "Coroutines launched by a cleanup handler are not canceled")
    late!!.cancel()
  }
}
