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
 * Obtain a scope through [FlixelSprite.animations]. The scope calls [FlixelSprite.ensureAnimation]
 * lazily, so the heavy controller is only allocated the first time a clip is registered. Do not use
 * this scope inside per-frame update code; register all clips during setup (for example inside
 * [FlixelState.create]).
 *
 * @property controller The animation controller clips are registered on.
 */
class FlixelAnimationScope(val controller: FlixelAnimationController) {

  /**
   * Registers an animation from a grid-frame index range.
   *
   * The range is converted to an `int[]` and passed to [FlixelAnimationController.addAnimation].
   * The [fps] parameter is the desired playback speed in frames-per-second; internally this is
   * stored as `1f / fps` (seconds per frame), which is what
   * [FlixelAnimationController.addAnimation] expects as its third argument.
   *
   * ```
   * sprite.animations {
   *   add("run", 0..5, fps = 12f)
   *   add("idle", 6..7, fps = 4f)
   * }
   * ```
   *
   * @param name The clip name used with [FlixelAnimationController.play].
   * @param frames The inclusive frame-index range to include in the clip.
   * @param fps The playback speed in frames per second; defaults to `30f`.
   */
  fun add(name: String, frames: IntRange, fps: Float = DEFAULT_FRAME_RATE.toFloat()) {
    val indices = frames.toList().toIntArray()
    controller.addAnimation(name, indices, 1f / fps)
  }

  /**
   * Registers an animation from an atlas prefix.
   *
   * Delegates to [FlixelAnimationController.addByPrefix]. The [frameRate] is the number of atlas
   * frames displayed per second (an integer, matching the Java API).
   *
   * ```
   * sprite.animations {
   *   addByPrefix("idle", "character_idle ", frameRate = 24, loop = true)
   * }
   * ```
   *
   * @param name The clip name.
   * @param prefix The frame-name prefix used to select atlas frames.
   * @param frameRate Frames per second; defaults to `30`.
   * @param loop Whether the clip should loop; defaults to `true`.
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
   * Registers an animation from explicit atlas frame indices.
   *
   * Delegates to [FlixelAnimationController.addFromAtlas]. The [frameDuration] is seconds-per-frame
   * (the reciprocal of fps), which is what the Java API expects.
   *
   * ```
   * sprite.animations {
   *   addFromAtlas("walk", intArrayOf(0, 1, 2, 3), frameDuration = 1f / 12f, loop = true)
   * }
   * ```
   *
   * @param name The clip name.
   * @param atlasFrameIndices Indices into the sprite's atlas frame list.
   * @param frameDuration Duration of each frame in seconds (use `1f / fps` to convert); defaults to
   *   `1f / 30f`.
   * @param loop Whether the clip should loop; defaults to `true`.
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
 * Opens a builder block for registering animations on this sprite.
 *
 * The sprite's animation controller is created lazily via [FlixelSprite.ensureAnimation] the first
 * time this function is called. All registrations happen at setup time; do not call this from
 * per-frame code.
 *
 * ```
 * sprite.animations {
 *   add("run", 0..5, fps = 12f)
 *   addByPrefix("idle", "idle ", frameRate = 24, loop = true)
 * }
 * ```
 *
 * @param block Receives a [FlixelAnimationScope] for registering clips.
 */
inline fun FlixelSprite.animations(block: FlixelAnimationScope.() -> Unit) {
  FlixelAnimationScope(ensureAnimation()).block()
}
