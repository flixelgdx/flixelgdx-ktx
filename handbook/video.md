# Videos: Waiting on Cutscenes with Coroutines

Intros, cutscenes, and story beats often go: play a video, wait for it to end, then move on. The
`flixelgdx-ktx-video` module lets a coroutine wait on a [FlixelGDX Video](https://github.com/flixelgdx/flixelgdx-video)
`FlixelVideo` the same way it waits on a tween or an animation. This guide covers adding the module, waiting for a
video to finish or to be ready, and what happens when a wait is canceled.

It builds on [Coroutines](coroutines.md), so read that first if `launch` and `await` are new to you. What a
`FlixelVideo` is, and how to create one, comes from the video extension's own handbook.

## Table of contents

1. [Why wait on a video?](#why-wait-on-a-video)
2. [Adding the module](#adding-the-module)
3. [Waiting for a video to finish](#waiting-for-a-video-to-finish)
4. [Playing and waiting in one call](#playing-and-waiting-in-one-call)
5. [Waiting for the first frame](#waiting-for-the-first-frame)
6. [Canceling, looping, and destroying](#canceling-looping-and-destroying)
7. [Common mistakes](#common-mistakes)
8. [Check your understanding](#check-your-understanding)

## Why wait on a video?

A video tells you it ended through its `onComplete` signal. Written with a listener, "play the intro, then go to the
menu" looks like this:

```kotlin
intro.onComplete += { Flixel.switchState { MenuState() } }
intro.play()
```

That works for one step, but each extra step (wait a moment, play a second clip) nests another listener. Written as
a coroutine, the same sequence reads in the order it happens:

```kotlin
launch {
  intro.playAndAwait()
  delay(0.5f)
  logoReel.playAndAwait()
  Flixel.switchState { MenuState() }
}
```

## Adding the module

Add `flixelgdx-ktx-video` next to `flixelgdx-ktx-core` (see [Getting Started](getting-started.md#choosing-modules)).
It brings in `flixelgdx-ktx-core` and the video extension's core module, so it works with every video backend. You
still add the backend for each platform yourself, as the video extension's handbook describes.

The helpers live in `org.flixelgdx.ktx.video`, and they run on the same dispatcher as the rest of the coroutine
support, so call `installFlixelCoroutines()` once before launching anything.

The video `await()` and the tween `await()` share a name but not a package. Import both when a file waits on tweens
and videos; Kotlin picks the right one from what you call it on.

## Waiting for a video to finish

`video.await()` pauses until the video reaches its end:

```kotlin
launch {
  val intro = FlixelVideos.create(Flixel.files.internal("videos/intro.mp4"))
  add(intro)
  intro.play()
  intro.await()
  Flixel.switchState { MenuState() }
}
```

If the video has already ended when you call it, `await()` returns right away instead of waiting for an ending that
already happened.

## Playing and waiting in one call

`video.playAndAwait()` calls `play()` and then waits, which is the most common pair:

```kotlin
launch {
  cutscene.playAndAwait()
  cutscene.destroy()
  startLevel()
}
```

It always waits for the playback it just started, even if the video finished an earlier playthrough. By default it
restarts the video from the beginning, the same as `play()` with no arguments. Pass `forceRestart = false` to let a
video that is already playing keep going from where it is.

## Waiting for the first frame

A video's width, height, and length are `0` until its first frame is decoded, shortly after the first `play()`.
Layout that depends on the size, such as centering, needs to wait for that moment. `video.awaitReady()` pauses until
the video is ready:

```kotlin
launch {
  trailer.play()
  trailer.awaitReady()
  trailer.screenCenter()
}
```

It checks once per frame, after the game updates, and creates no objects while it waits. If the video is already
ready, it returns right away.

## Canceling, looping, and destroying

Every video wait removes its listener when it resumes or when its coroutine is canceled (by `job.cancel()`,
`cancelAllCoroutines()`, or a state switch). Canceling never leaves a stale listener on the video.

Two situations never resume on their own:

- **A looping video never finishes.** `onComplete` only fires for videos that are not looping, so `await()` on a
  looping video waits until the coroutine is canceled.
- **A destroyed video never finishes either.** `destroy()` clears the video's signals, so a coroutine waiting on it
  stays paused until it is canceled. A state switch cancels it for you.

A video with `setAutoDestroy(true)` is fine to await: it announces that it finished first, then destroys itself.

## Common mistakes

- **Awaiting a looping video.** It never ends. Turn looping off first, or wait on something else, such as a delay or
  a button press.
- **Centering before the video is ready.** `screenCenter()` before the first frame uses a size of `0`, so the video
  lands off center. Await `awaitReady()` first.
- **Forgetting `installFlixelCoroutines()`.** Without it, nothing runs the coroutine, so the video plays but the code
  after `await()` never does.

## Check your understanding

1. You call `video.screenCenter()` right after `video.play()`, and the video appears off center. Why, and what is
   the fix?
2. A coroutine awaits a background video and never moves on. The video is playing fine. What is the likely cause?
3. Do you need to remove anything by hand when the state switches while a coroutine is waiting on a video?

(Answers: the size is `0` until the first frame is decoded, so call `awaitReady()` before centering; the video is
looping, so it never fires `onComplete`; no, the state switch cancels the coroutine and the wait removes its own
listener.)

**Next:** ["How Do I...?" Cookbook](cookbook.md)
