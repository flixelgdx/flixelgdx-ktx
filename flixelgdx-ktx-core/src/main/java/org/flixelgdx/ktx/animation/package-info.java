/// A builder DSL for registering animation clips on a [FlixelSprite][org.flixelgdx.FlixelSprite].
///
/// Open a block with `animations { }` and register every clip in it. The sprite's animation
/// controller is created the first time a clip is added, so sprites without animations never pay
/// for one. Register clips during setup (for example in `create()`), not in per-frame code.
///
/// ## Frame ranges
///
/// For a sprite sheet split into equal frames, pass the frame indices as a range:
///
/// ```kotlin
/// sprite.animations {
///   add("run", 0..5, fps = 12f)
///   add("idle", 6..7, fps = 4f)
/// }
/// ```
///
/// ## Texture atlases
///
/// For a sprite loaded from an atlas, register clips by their frame name prefix, or by explicit
/// atlas frame indices:
///
/// ```kotlin
/// sprite.animations {
///   addByPrefix("idle", "character_idle ", frameRate = 24, loop = true)
///   addFromAtlas("walk", intArrayOf(0, 1, 2, 3), frameDuration = 1f / 12f, loop = true)
/// }
/// ```
///
/// To wait for a clip to finish inside a coroutine, see `await(name)` in
/// `org.flixelgdx.ktx.async`.
///
/// @see org.flixelgdx.animation.FlixelAnimationController
package org.flixelgdx.ktx.animation;
