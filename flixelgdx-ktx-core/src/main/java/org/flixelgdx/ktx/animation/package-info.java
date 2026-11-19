/// A builder DSL for registering animation clips on a [FlixelSprite][org.flixelgdx.FlixelSprite].
///
/// Open a block with `animations { }` and register every clip in it. Each method in the block has
/// the same name as the [FlixelAnimationController][org.flixelgdx.animation.FlixelAnimationController]
/// method it calls, with default arguments filled in (30 frames per second, looping). The
/// sprite's controller is created when the block opens, so sprites that never open one never pay
/// for it. Register clips during setup (for example in `create()`), not in per-frame code.
///
/// ## Frame numbers
///
/// For a sprite sheet split into equal frames, pass the frame numbers as a range or an array.
/// Frames are counted left to right, then top to bottom, starting at `0`:
///
/// ```kotlin
/// sprite.animations {
///   add("run", 0..5, frameRate = 12)
///   add("idle", intArrayOf(6, 7, 7, 6), frameRate = 4)
///   add("jump", 8..11, frameRate = 12, loop = false)
/// }
/// ```
///
/// ## Texture atlases
///
/// For a sprite loaded from an atlas, register clips by frame name prefix, by the numbers in
/// their frame names, or by explicit atlas frame indices:
///
/// ```kotlin
/// sprite.animations {
///   addByPrefix("idle", "character_idle ", frameRate = 24)
///   addByIndices("danceLeft", "GF Dancing Beat", 0..7, frameRate = 24, loop = false)
///   addFromAtlas("walk", intArrayOf(0, 1, 2, 3), frameDuration = 1f / 12f)
/// }
/// ```
///
/// To wait for a clip to finish inside a coroutine, see `await(name)` in
/// `org.flixelgdx.ktx.async`.
///
/// @see org.flixelgdx.animation.FlixelAnimationController
package org.flixelgdx.ktx.animation;
