/// Coroutine helpers for videos from the [FlixelGDX Video](https://github.com/flixelgdx/flixelgdx-video)
/// extension.
///
/// These functions let a coroutine wait on a [FlixelVideo][org.flixelgdx.video.FlixelVideo]
/// instead of wiring callbacks to its signals. They run on the same dispatcher as the rest of
/// `org.flixelgdx.ktx.async`, so call `installFlixelCoroutines()` once before launching anything.
///
/// ## Waiting for a video to finish
///
/// `await()` suspends until a non-looping video reaches its end, and `playAndAwait()` starts the
/// video and waits in one call. Both are a natural fit for intros and cutscenes:
///
/// ```kotlin
/// launch {
///   val intro = FlixelVideos.create(Flixel.files.internal("videos/intro.mp4"))
///   add(intro)
///   intro.playAndAwait()
///   Flixel.switchState { MenuState() }
/// }
/// ```
///
/// A looping video never finishes, so waiting on one suspends until the coroutine is canceled.
///
/// ## Waiting for a video to be ready
///
/// A video's size is `0` until its first frame is decoded. `awaitReady()` suspends until then, so
/// layout that depends on the size runs once, at the right time:
///
/// ```kotlin
/// launch {
///   video.play()
///   video.awaitReady()
///   video.screenCenter()
/// }
/// ```
///
/// @see org.flixelgdx.video.FlixelVideo
package org.flixelgdx.ktx.video;
