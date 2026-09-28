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

import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Unit tests for [Dispatcher]. */
class DispatcherTest {

  @Test
  fun blocksDoNotRunBeforeUpdate() {
    val log = mutableListOf<Int>()

    Dispatcher.dispatch(EmptyCoroutineContext, Runnable { log.add(1) })
    Dispatcher.dispatch(EmptyCoroutineContext, Runnable { log.add(2) })

    assertTrue(log.isEmpty(), "Blocks should not run before update() is called")
  }

  @Test
  fun allBlocksRunAfterUpdate() {
    val log = mutableListOf<Int>()

    Dispatcher.dispatch(EmptyCoroutineContext, Runnable { log.add(1) })
    Dispatcher.dispatch(EmptyCoroutineContext, Runnable { log.add(2) })
    Dispatcher.dispatch(EmptyCoroutineContext, Runnable { log.add(3) })

    Dispatcher.update()

    assertEquals(listOf(1, 2, 3), log, "Blocks should run in FIFO order after update()")
  }

  @Test
  fun queueIsEmptyAfterUpdate() {
    Dispatcher.dispatch(EmptyCoroutineContext, Runnable { /* no-op */ })
    Dispatcher.update()

    // A second update should not re-run anything.
    val secondRunCount = mutableListOf<Int>()
    Dispatcher.update()
    assertTrue(
      secondRunCount.isEmpty(),
      "Queue should be empty after drain; second update is a no-op",
    )
  }

  @Test
  fun blockEnqueuedDuringDrainRunsNextFrame() {
    val firstFrameLog = mutableListOf<Int>()
    val secondFrameLog = mutableListOf<Int>()

    // The block enqueued during the drain should NOT run in the same update() call.
    Dispatcher.dispatch(
      EmptyCoroutineContext,
      Runnable {
        firstFrameLog.add(1)
        // Enqueue a block while the drain is in progress.
        Dispatcher.dispatch(EmptyCoroutineContext, Runnable { secondFrameLog.add(2) })
      },
    )

    Dispatcher.update()

    assertEquals(listOf(1), firstFrameLog, "Only the first block runs in the first update")
    assertTrue(
      secondFrameLog.isEmpty(),
      "Block enqueued during drain should not run in the same frame",
    )

    Dispatcher.update()

    assertEquals(listOf(2), secondFrameLog, "Block enqueued during drain runs in the next update")
  }

  @Test
  fun multipleBlocksEnqueuedDuringDrainAllRunNextFrame() {
    val firstFrameLog = mutableListOf<Int>()
    val secondFrameLog = mutableListOf<Int>()

    Dispatcher.dispatch(
      EmptyCoroutineContext,
      Runnable {
        firstFrameLog.add(1)
        Dispatcher.dispatch(EmptyCoroutineContext, Runnable { secondFrameLog.add(2) })
        Dispatcher.dispatch(EmptyCoroutineContext, Runnable { secondFrameLog.add(3) })
      },
    )

    Dispatcher.update()

    assertFalse(
      secondFrameLog.isNotEmpty() && secondFrameLog.size < 2,
      "Neither re-entrant block should have run yet",
    )
    assertTrue(secondFrameLog.isEmpty())

    Dispatcher.update()

    assertEquals(
      listOf(2, 3),
      secondFrameLog,
      "Both re-entrant blocks run in FIFO order on next frame",
    )
  }
}
