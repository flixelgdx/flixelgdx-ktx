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
@file:JvmName("FlixelAnimations")

package org.flixelgdx.ktx.animation

import org.flixelgdx.FlixelSprite
import org.flixelgdx.FlixelState
import org.flixelgdx.animation.FlixelAnimationController

// Frames-per-second applied when a clip is registered without an explicit rate.
private const val DEFAULT_FRAME_RATE = 30

/**
 * A builder scope for registering animation clips on a [FlixelAnimationController].
 *
 * Obtain a scope through [FlixelSprite.animations]. Every method mirrors the controller method of
 * the same name, adding Kotlin default arguments and [IntRange] overloads. Do not use this scope
 * inside per-frame update code; register all clips during setup (for example inside
 * [FlixelState.create]).
 *
 * @property controller The animation controller clips are registered on.
 */
class FlixelAnimationScope(val controller: FlixelAnimationController) {

  /**
   * Registers a clip from frame numbers in the sprite's own frame list.
   *
   * Delegates to [FlixelAnimationController.add]. For a sprite sheet cut into a grid, frames are
   * numbered left to right, then top to bottom, starting at `0`. For a sprite with an atlas, frames
   * are numbered in atlas order. Numbers may repeat to hold a pose, and numbers that do not exist
   * are skipped.
   *
   * ```
   * sprite.animations {
   *   add("idle", intArrayOf(0, 1, 1, 0), frameRate = 4)
   * }
   * ```
   *
   * @param name The clip name used with [FlixelAnimationController.play].
   * @param frames Frame numbers to play, in order.
   * @param frameRate How many frames the clip shows per second; defaults to `30`.
   * @param loop Whether the clip loops; defaults to `true`.
   */
  fun add(
    name: String,
    frames: IntArray,
    frameRate: Int = DEFAULT_FRAME_RATE,
    loop: Boolean = true,
  ) {
    controller.add(name, frames, frameRate, loop)
  }

  /**
   * Registers a clip from a range of frame numbers in the sprite's own frame list.
   *
   * Same as the [IntArray] overload of [add], but plays every frame in [frames] in order.
   *
   * ```
   * sprite.animations {
   *   add("run", 0..5, frameRate = 12)
   *   add("jump", 6..9, frameRate = 12, loop = false)
   * }
   * ```
   *
   * @param name The clip name used with [FlixelAnimationController.play].
   * @param frames The inclusive range of frame numbers to play.
   * @param frameRate How many frames the clip shows per second; defaults to `30`.
   * @param loop Whether the clip loops; defaults to `true`.
   */
  fun add(
    name: String,
    frames: IntRange,
    frameRate: Int = DEFAULT_FRAME_RATE,
    loop: Boolean = true,
  ) {
    controller.add(name, frames.toFrameArray(), frameRate, loop)
  }

  /**
   * Registers a clip from atlas frames picked by the number in their names.
   *
   * Delegates to [FlixelAnimationController.addByIndices]. A frame matches number `n` when its name
   * is [prefix], then digits whose value is `n`, then [postfix]. Leading zeros are ignored, so `3`
   * matches both `"idle3"` and `"idle0003"`.
   *
   * ```
   * sprite.animations {
   *   addByIndices("danceLeft", "GF Dancing Beat", intArrayOf(0, 1, 2, 3), loop = false)
   * }
   * ```
   *
   * @param name The clip name used with [FlixelAnimationController.play].
   * @param prefix The text every frame name in the clip starts with.
   * @param indices Frame numbers to play, in order.
   * @param postfix The text every frame name in the clip ends with; defaults to none.
   * @param frameRate How many frames the clip shows per second; defaults to `30`.
   * @param loop Whether the clip loops; defaults to `true`.
   */
  fun addByIndices(
    name: String,
    prefix: String,
    indices: IntArray,
    postfix: String = "",
    frameRate: Int = DEFAULT_FRAME_RATE,
    loop: Boolean = true,
  ) {
    controller.addByIndices(name, prefix, indices, postfix, frameRate, loop)
  }

  /**
   * Registers a clip from a range of numbered atlas frames.
   *
   * Same as the [IntArray] overload of [addByIndices], but plays every number in [indices] in
   * order.
   *
   * ```
   * sprite.animations {
   *   addByIndices("danceLeft", "GF Dancing Beat", 0..7, frameRate = 24, loop = false)
   *   addByIndices("danceRight", "GF Dancing Beat", 8..14, frameRate = 24, loop = false)
   * }
   * ```
   *
   * @param name The clip name used with [FlixelAnimationController.play].
   * @param prefix The text every frame name in the clip starts with.
   * @param indices The inclusive range of frame numbers to play.
   * @param postfix The text every frame name in the clip ends with; defaults to none.
   * @param frameRate How many frames the clip shows per second; defaults to `30`.
   * @param loop Whether the clip loops; defaults to `true`.
   */
  fun addByIndices(
    name: String,
    prefix: String,
    indices: IntRange,
    postfix: String = "",
    frameRate: Int = DEFAULT_FRAME_RATE,
    loop: Boolean = true,
  ) {
    controller.addByIndices(name, prefix, indices.toFrameArray(), postfix, frameRate, loop)
  }

  /**
   * Registers a clip from every atlas frame whose name starts with [prefix].
   *
   * Delegates to [FlixelAnimationController.addByPrefix]. Matching frames are sorted by name.
   *
   * ```
   * sprite.animations {
   *   addByPrefix("idle", "character_idle ", frameRate = 24)
   * }
   * ```
   *
   * @param name The clip name used with [FlixelAnimationController.play].
   * @param prefix The frame-name prefix used to select atlas frames.
   * @param frameRate How many frames the clip shows per second; defaults to `30`.
   * @param loop Whether the clip loops; defaults to `true`.
   */
  fun addByPrefix(
    name: String,
    prefix: String,
    frameRate: Int = DEFAULT_FRAME_RATE,
    loop: Boolean = true,
  ) {
    controller.addByPrefix(name, prefix, frameRate, loop)
  }

  /**
   * Registers a clip from explicit atlas frame indices.
   *
   * Delegates to [FlixelAnimationController.addFromAtlas]. Unlike the other methods, the timing is
   * given as [frameDuration] in seconds per frame, which is what the Java API expects. Most clips
   * read more clearly with [add], which takes a frame rate instead.
   *
   * ```
   * sprite.animations {
   *   addFromAtlas("walk", intArrayOf(0, 1, 2, 3), frameDuration = 1f / 12f)
   * }
   * ```
   *
   * @param name The clip name used with [FlixelAnimationController.play].
   * @param atlasFrameIndices Indices into the sprite's atlas frame list.
   * @param frameDuration Duration of each frame in seconds (use `1f / fps` to convert); defaults to
   *   `1f / 30f`.
   * @param loop Whether the clip loops; defaults to `true`.
   */
  fun addFromAtlas(
    name: String,
    atlasFrameIndices: IntArray,
    frameDuration: Float = 1f / DEFAULT_FRAME_RATE,
    loop: Boolean = true,
  ) {
    controller.addFromAtlas(name, atlasFrameIndices, frameDuration, loop)
  }
}

/**
 * Converts this range to the `int[]` the Java API takes, without boxing each number.
 *
 * @return A new array holding every number in the range, in order; empty for an empty range.
 */
internal fun IntRange.toFrameArray(): IntArray =
  if (isEmpty()) IntArray(0) else IntArray(last - first + 1) { first + it }

/**
 * Opens a builder block for registering animations on this sprite.
 *
 * The sprite's animation controller is created lazily via [FlixelSprite.ensureAnimation] the first
 * time this function is called. All registrations happen at setup time; do not call this from
 * per-frame code.
 *
 * ```
 * sprite.animations {
 *   add("run", 0..5, frameRate = 12)
 *   addByPrefix("idle", "idle ", frameRate = 24)
 *   addByIndices("wave", "idle ", intArrayOf(0, 1, 2, 1, 0), loop = false)
 * }
 * ```
 *
 * @param block Receives a [FlixelAnimationScope] for registering clips.
 */
inline fun FlixelSprite.animations(block: FlixelAnimationScope.() -> Unit) {
  FlixelAnimationScope(ensureAnimation()).block()
}
