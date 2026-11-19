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
package org.flixelgdx.ktx.animation

import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import org.flixelgdx.FlixelSprite
import org.flixelgdx.animation.FlixelAnimation
import org.flixelgdx.asset.FlixelBaseAssetManager
import org.flixelgdx.collections.FlixelArray
import org.flixelgdx.graphics.FlixelFrame
import org.flixelgdx.graphics.FlixelGraphic
import org.flixelgdx.graphics.FlixelNoopTexture
import org.junit.jupiter.api.Test

class FlixelAnimationsTest {

  @Test
  fun `toFrameArray lists every number in the range in order`() {
    assertContentEquals(intArrayOf(0, 1, 2, 3, 4, 5), (0..5).toFrameArray())
    assertContentEquals(intArrayOf(3), (3..3).toFrameArray())
    assertContentEquals(intArrayOf(), IntRange.EMPTY.toFrameArray())
  }

  @Test
  fun `add with a range registers those frames at the given rate`() {
    val sprite = atlasSprite("a", "b", "c", "d")
    val atlas = sprite.atlasRegions

    sprite.animations { add("clip", 1..2, frameRate = 10, loop = false) }

    val clip = clip(sprite, "clip")
    val frames = clip.keyFrames
    assertEquals(2, frames.size)
    assertSame(atlas.get(1), frames[0])
    assertSame(atlas.get(2), frames[1])
    assertEquals(0.1f, clip.frameDuration, 0.0001f)
    assertEquals(FlixelAnimation.PlayMode.NORMAL, clip.playMode)
  }

  @Test
  fun `add with an array keeps order and repeats and loops by default`() {
    val sprite = atlasSprite("a", "b", "c")
    val atlas = sprite.atlasRegions

    sprite.animations { add("clip", intArrayOf(2, 0, 2)) }

    val clip = clip(sprite, "clip")
    val frames = clip.keyFrames
    assertEquals(3, frames.size)
    assertSame(atlas.get(2), frames[0])
    assertSame(atlas.get(0), frames[1])
    assertSame(atlas.get(2), frames[2])
    assertEquals(FlixelAnimation.PlayMode.LOOP, clip.playMode)
  }

  @Test
  fun `addByIndices picks numbered frames by name`() {
    val sprite = atlasSprite("dance0000", "dance0001", "dance0002", "dance-alt0001")
    val atlas = sprite.atlasRegions

    sprite.animations {
      addByIndices("left", "dance", 0..1)
      addByIndices("back", "dance", intArrayOf(2, 1), frameRate = 24)
    }

    val left = clip(sprite, "left").keyFrames
    assertEquals(2, left.size)
    assertSame(atlas.get(0), left[0])
    assertSame(atlas.get(1), left[1])

    val back = clip(sprite, "back").keyFrames
    assertSame(atlas.get(2), back[0])
    assertSame(atlas.get(1), back[1])
  }

  @Test
  fun `addByIndices honors the postfix`() {
    val sprite = atlasSprite("run0000.png", "run0001.png", "run0001")
    val atlas = sprite.atlasRegions

    sprite.animations { addByIndices("run", "run", intArrayOf(1), postfix = ".png") }

    val frames = clip(sprite, "run").keyFrames
    assertEquals(1, frames.size)
    assertSame(atlas.get(1), frames[0])
  }

  @Test
  fun `addByPrefix and addFromAtlas still delegate`() {
    val sprite = atlasSprite("idle1", "idle0", "walk0")

    sprite.animations {
      addByPrefix("idle", "idle")
      addFromAtlas("walk", intArrayOf(2), frameDuration = 0.5f, loop = false)
    }

    assertEquals(2, clip(sprite, "idle").keyFrames.size)
    assertEquals(0.5f, clip(sprite, "walk").frameDuration, 0.0001f)
    assertFalse(sprite.ensureAnimation().animations.containsKey("missing"))
  }

  private fun clip(sprite: FlixelSprite, name: String): FlixelAnimation<*> =
    checkNotNull(sprite.ensureAnimation().animations.get(name)) { "No clip named $name" }

  private fun atlasSprite(vararg names: String): FlixelSprite {
    val texture = FlixelNoopTexture(64, 64)
    val frames = FlixelArray<FlixelFrame>()
    for (name in names) {
      val frame = FlixelFrame(texture, 0, 0, 8, 8)
      frame.name = name
      frames.add(frame)
    }
    val assets = FlixelBaseAssetManager()
    val sprite = FlixelSprite()
    sprite.ensureAnimation()
    sprite.applySparrowAtlas(FlixelGraphic(assets, assets.allocateSyntheticKey(), texture), frames)
    return sprite
  }
}
