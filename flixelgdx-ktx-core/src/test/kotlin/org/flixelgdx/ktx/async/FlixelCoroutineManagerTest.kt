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

import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.flixelgdx.Flixel
import org.flixelgdx.FlixelManagers
import org.flixelgdx.signal.FlixelSignal
import org.flixelgdx.signal.FlixelSignalData.StateSwitchSignalData

class FlixelCoroutineManagerTest {

  @AfterTest
  fun tearDown() {
    cancelAllCoroutines()
    Dispatcher.update()
    Flixel.managers.remove(FlixelCoroutineManager)
  }

  @Test
  fun installRegistersTheManagerOnce() {
    val before = Flixel.managers.size

    installFlixelCoroutines()
    installFlixelCoroutines()

    assertTrue(Flixel.managers.contains(FlixelCoroutineManager))
    assertEquals(before + 1, Flixel.managers.size)
  }

  @Test
  fun postUpdateRunsTheDispatcher() {
    var ran = false
    launch { ran = true }

    FlixelCoroutineManager.postUpdate(0f)

    assertTrue(ran)
  }

  @Test
  fun preStateSwitchCancelsEveryCoroutine() {
    val signal = FlixelSignal<String>()
    val job = launch { signal.awaitOnce() }
    Dispatcher.update()

    FlixelCoroutineManager.preStateSwitch(StateSwitchSignalData(null))

    assertTrue(job.isCancelled)
  }

  @Test
  fun destroyCancelsEveryCoroutine() {
    val signal = FlixelSignal<String>()
    val job = launch { signal.awaitOnce() }
    Dispatcher.update()

    FlixelCoroutineManager.destroy()

    assertTrue(job.isCancelled)
  }

  @Test
  fun coroutinesWaitWhileTheGameIsPaused() {
    val managers = FlixelManagers()
    managers.add(FlixelCoroutineManager)
    var ran = false
    launch { ran = true }

    managers.postUpdate(0f, true)
    assertFalse(ran, "a paused game does not resume coroutines")

    managers.postUpdate(0f, false)
    assertTrue(ran, "coroutines resume once the game is unpaused")
  }

  @Test
  fun hasItsOwnProfilerName() {
    assertEquals("ktx.coroutines", FlixelCoroutineManager.name)
    assertTrue(FlixelCoroutineManager.isPausable)
  }
}
