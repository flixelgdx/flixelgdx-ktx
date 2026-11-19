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
package org.flixelgdx.ktx.video

import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.flixelgdx.Flixel
import org.flixelgdx.graphics.FlixelTexture
import org.flixelgdx.ktx.async.Dispatcher
import org.flixelgdx.ktx.async.cancelAllCoroutines
import org.flixelgdx.ktx.async.launch
import org.flixelgdx.signal.FlixelSignalData.UpdateSignalData
import org.flixelgdx.video.FlixelVideo
import org.flixelgdx.video.FlixelVideoPlayer
import org.flixelgdx.video.FlixelVideoQuality

/**
 * Tests for the video coroutine helpers, driven by a fake [FlixelVideoPlayer] so no decoder or game
 * loop is needed. Each test pumps [Dispatcher.update] by hand, the same way the async tests in
 * `flixelgdx-ktx-core` do.
 */
class FlixelVideoCoroutinesTest {

  private val tick = UpdateSignalData()

  @AfterTest
  fun cancelLeftovers() {
    cancelAllCoroutines()
  }

  @Test
  fun awaitResumesWhenTheVideoCompletes() {
    val player = FakePlayer()
    val video = FlixelVideo(player)
    var done = false

    launch {
      video.await()
      done = true
    }
    Dispatcher.update()
    video.update(0f)
    Dispatcher.update()
    assertFalse(done, "Should still wait while the video is playing")

    player.ended = true
    video.update(0f)
    Dispatcher.update()
    assertTrue(done, "Should resume once onComplete fires")
  }

  @Test
  fun awaitReturnsRightAwayWhenTheVideoAlreadyEnded() {
    val player = FakePlayer()
    player.ended = true
    val video = FlixelVideo(player)
    var done = false

    launch {
      video.await()
      done = true
    }
    Dispatcher.update()
    assertTrue(done)
  }

  @Test
  fun awaitWaitsForeverOnALoopingVideo() {
    val player = FakePlayer()
    player.loop = true
    player.ended = true
    val video = FlixelVideo(player)
    var done = false

    launch {
      video.await()
      done = true
    }
    Dispatcher.update()
    video.update(0f)
    Dispatcher.update()
    assertFalse(done)
  }

  @Test
  fun canceledAwaitDoesNotResumeAndLeavesNoHandler() {
    val player = FakePlayer()
    val video = FlixelVideo(player)
    var done = false

    launch {
      video.await()
      done = true
    }
    Dispatcher.update()
    cancelAllCoroutines()

    var otherCalls = 0
    video.onComplete.add { otherCalls++ }
    player.ended = true
    video.update(0f)
    Dispatcher.update()
    assertFalse(done, "A canceled coroutine must not resume")
    assertEquals(1, otherCalls, "Other listeners still run")
  }

  @Test
  fun playAndAwaitPlaysAndWaitsEvenAfterAnEarlierEnding() {
    val player = FakePlayer()
    player.ended = true
    val video = FlixelVideo(player)
    var done = false

    launch {
      video.playAndAwait()
      done = true
    }
    Dispatcher.update()
    assertEquals(1, player.plays, "Should call play once")
    assertEquals(0f, player.timeMs, "The default restarts from the beginning")
    assertFalse(done, "Should wait for this playback, not the earlier ending")

    player.ended = true
    video.update(0f)
    Dispatcher.update()
    assertTrue(done)
  }

  @Test
  fun awaitReadyResumesOnTheFirstFrameAfterTheVideoIsReady() {
    val player = FakePlayer()
    val video = FlixelVideo(player)
    var done = false

    launch {
      video.awaitReady()
      done = true
    }
    Dispatcher.update()
    Flixel.Signals.postUpdate.dispatch(tick)
    Dispatcher.update()
    assertFalse(done)

    player.ready = true
    Flixel.Signals.postUpdate.dispatch(tick)
    Dispatcher.update()
    assertTrue(done)
  }

  @Test
  fun awaitReadyReturnsRightAwayWhenAlreadyReady() {
    val player = FakePlayer()
    player.ready = true
    val video = FlixelVideo(player)
    var done = false

    launch {
      video.awaitReady()
      done = true
    }
    Dispatcher.update()
    assertTrue(done)
  }

  @Test
  fun canceledAwaitReadyStopsPolling() {
    val player = FakePlayer()
    val video = FlixelVideo(player)
    var done = false

    launch {
      video.awaitReady()
      done = true
    }
    Dispatcher.update()
    cancelAllCoroutines()

    player.ready = true
    Flixel.Signals.postUpdate.dispatch(tick)
    Dispatcher.update()
    assertFalse(done)
  }

  /** A player whose state the tests set directly. */
  private class FakePlayer : FlixelVideoPlayer {
    var ready = false
    var ended = false
    var loop = false
    var plays = 0
    var timeMs = -1f

    override fun play() {
      plays++
      ended = false
    }

    override fun pause() {}

    override fun resume() {}

    override fun stop() {}

    override fun isPlaying(): Boolean = plays > 0 && !ended

    override fun isReady(): Boolean = ready

    override fun isEnded(): Boolean = ended

    override fun getTime(): Float = timeMs

    override fun setTime(timeMs: Float) {
      this.timeMs = timeMs
    }

    override fun getLength(): Float = 0f

    override fun getRate(): Float = 1f

    override fun setRate(rate: Float) {}

    override fun isLooped(): Boolean = loop

    override fun setLooped(looped: Boolean) {
      loop = looped
    }

    override fun getVolume(): Float = 1f

    override fun setVolume(volume: Float) {}

    override fun setQuality(quality: FlixelVideoQuality) {}

    override fun update(elapsed: Float) {}

    override fun getFrame(): FlixelTexture? = null

    override fun getVideoWidth(): Int = 0

    override fun getVideoHeight(): Int = 0

    override fun getFrameWidth(): Int = 0

    override fun getFrameHeight(): Int = 0

    override fun destroy() {}
  }
}
