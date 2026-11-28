# Coroutines: Writing Timed Sequences Top to Bottom

Cutscenes, tutorials, and attack patterns are sequences: do this, wait, do that, wait until the player clicks. Written
with callbacks, a sequence is scattered across many functions. Written as a coroutine, it reads top to bottom. This
guide covers starting coroutines, waiting for time, tweens, signals, and animations, canceling them, and writing your
own waits. Games that use FlixelGDX Video can wait on videos too; see [Videos](video.md).

## Table of contents

1. [Why coroutines?](#why-coroutines)
2. [Installing the dispatcher](#installing-the-dispatcher)
3. [Launching a coroutine](#launching-a-coroutine)
4. [Waiting for time](#waiting-for-time)
5. [Awaiting a tween](#awaiting-a-tween)
6. [Awaiting a signal](#awaiting-a-signal)
7. [Awaiting an animation](#awaiting-an-animation)
8. [Canceling](#canceling)
9. [Errors](#errors)
10. [Writing your own await](#writing-your-own-await)
11. [Why not kotlinx-coroutines?](#why-not-kotlinx-coroutines)
12. [Common mistakes](#common-mistakes)
13. [Check your understanding](#check-your-understanding)

## Why coroutines?

Here is a short intro sequence written with callbacks:

```kotlin
logo.tween(duration = 1f, onComplete = {
  FlixelTimer().start(2f) {
    title.tween(duration = 0.5f, onComplete = { showMenu() }) {
      goal(title::getAlpha, 1f, title::setAlpha)
    }
  }
}) {
  goal(logo::getY, 100f, logo::setY)
}
```

And here it is as a coroutine:

```kotlin
launch {
  logo.tween(duration = 1f) { goal(logo::getY, 100f, logo::setY) }.await()
  delay(2f)
  title.tween(duration = 0.5f) { goal(title::getAlpha, 1f, title::setAlpha) }.await()
  showMenu()
}
```

A coroutine is a function that can **pause** in the middle (at `delay`, `await`, and similar calls) and pick up where
it left off on a later frame. Think of a bookmark in a recipe: you stop at "let the dough rise for an hour," do other
things, and come back to the exact line you left.

Everything runs on the game thread, one frame at a time. There are no background threads, so a coroutine can touch
sprites, groups, and the UI freely.

## Installing the dispatcher

Call `installFlixelCoroutines()` once, before the first `launch`. A good place is your first state's `create()`, or
your game class:

```kotlin
override fun create() {
  super.create()
  installFlixelCoroutines()
}
```

It registers `FlixelCoroutineManager` in `Flixel.managers`, which does three things:

- Hooks the coroutine dispatcher into the game loop, so waiting coroutines resume right after the state updates.
- **Cancels every running coroutine whenever the state switches**, so a sequence from the last level never keeps
  running in the next one.
- Holds every coroutine in place while the game is paused (for example by the debugger), the same way tweens and
  timers stop, and lets them carry on when the game resumes.

Calling it again later is harmless; every call after the first does nothing.

## Launching a coroutine

`launch { }` starts a coroutine and returns its `FlixelJob`:

```kotlin
val spawner = launch {
  while (isActive) {
    spawnEnemy()
    delay(2f)
  }
}
```

- The body starts on the **next** dispatcher pass, not inside the `launch` call itself.
- Inside the body, `this` is the job, so `isActive` and `cancel()` refer to the coroutine itself.
- From outside, the job tells you its state: `isActive`, `isCancelled`, and `isCompleted`.

Each `launch` creates a coroutine object, so launch sequences from `create()`, event handlers, and other one-off
moments, never from `update()`. A coroutine that loops with `delay` (like the spawner above) replaces per-frame timer
code without creating anything per frame.

## Waiting for time

`delay(seconds)` pauses the coroutine for that many seconds of **game time**:

```kotlin
launch {
  showBanner("Get ready!")
  delay(3f)
  hideBanner()
}
```

Game time follows `Flixel.timeScale`, so slow motion slows the delay down and pausing the game pauses it. A delay of
zero or less resumes on the next update.

## Awaiting a tween

`tween.await()` pauses until the tween finishes:

```kotlin
launch {
  val slide = panel.tween(duration = 0.4f) { goal(panel::getX, 20f, panel::setX) }
  slide.await()
  Flixel.info("The panel is in place.")
}
```

`await()` registers through `then`, so it does not replace an `onComplete` callback you already set. It never returns
for `LOOPING` or `PINGPONG` tweens, because they never finish. If the coroutine is canceled while waiting, the tween
keeps running; cancel the tween too if you want it to stop.

## Awaiting a signal

`signal.awaitOnce()` pauses until the signal is dispatched once, and returns the value it carried:

```kotlin
val onDialogClosed = FlixelSignal<Unit>()

launch {
  showDialog("Welcome to the village!")
  onDialogClosed.awaitOnce()
  Flixel.switchState { VillageState() }
}

// Elsewhere, when the player closes the dialog:
onDialogClosed.dispatch(Unit)
```

It works with the framework's global signals too:

```kotlin
launch {
  val data = Flixel.Signals.preStateSwitch.awaitOnce()
  Flixel.info("Leaving {}", data.state())
}
```

When the signal is dispatched with no data (`dispatch()`), the returned value is `null`. If the coroutine is canceled
first, its one-time listener stays on the signal until the next dispatch, then does nothing.

## Awaiting an animation

`animation.await(name)` pauses until the named clip finishes on that sprite, and returns the frame data of its last
frame:

```kotlin
launch {
  boss.animation?.play("roar")
  boss.animation?.await("roar")
  boss.animation?.play("idle")
}
```

Other clips finishing in the meantime are ignored. Only clips that end fire the finished event, so do not await a
looping clip. If the coroutine is canceled, its listener is removed.

## Canceling

There are three ways a coroutine is canceled:

| How                            | When to use it                                               |
|--------------------------------|--------------------------------------------------------------|
| `job.cancel()`                 | Stop one sequence, such as an enemy spawner, early.          |
| `cancelAllCoroutines()`        | Stop everything, for example when opening a game-over screen. |
| A state switch                 | Automatic, once `installFlixelCoroutines()` has run.         |

Canceling makes the coroutine throw a `CancellationException` at the point where it is paused (or the next one it
reaches). That means `finally` blocks still run, which is the right place for cleanup:

```kotlin
val music = launch {
  try {
    while (isActive) {
      pulseSpeakers()
      delay(0.5f)
    }
  } finally {
    resetSpeakers()
  }
}

// Later:
music.cancel()
```

The waits in the extension clean up after themselves when canceled: `delay` cancels its timer, and `animation.await`
removes its listener. Canceling a job that already finished does nothing.

## Errors

If a coroutine throws an exception (other than a cancellation), the exception travels out of the game loop exactly
like an exception thrown from `update()`, so your game's normal crash handling sees it.

## Writing your own await

`suspendCancellable { cont -> }` is the building block behind every wait in the extension, and you can use it to make
your own. Hand the continuation to a callback that calls `cont.resume(value)` when the event happens, and undo the
setup in `cont.invokeOnCancellation { }`:

```kotlin
suspend fun FlixelSignal<Int>.awaitScoreAbove(target: Int): Int = suspendCancellable { cont ->
  val handler = object : FlixelSignal.SignalHandler<Int> {
    override fun execute(score: Int?) {
      if (score != null && score > target) {
        remove(this)
        cont.resume(score)
      }
    }
  }
  add(handler)
  cont.invokeOnCancellation { remove(handler) }
}
```

`resume` is the standard `kotlin.coroutines.resume` function. Only the first resume counts; later ones, and resumes
after cancellation, are ignored. `cont.isActive` tells you whether the coroutine is still waiting.

## Why not kotlinx-coroutines?

The well-known `kotlinx-coroutines` library relies on threads, locks, and atomic operations that the web build cannot
compile. The extension's coroutines use only the coroutine support built into Kotlin itself, so they behave the same
on desktop, Android, and the web. That is why the extension has its own `launch` and `delay`:

- Import them from `org.flixelgdx.ktx.async`, not from `kotlinx.coroutines`.
- Do not add `kotlinx-coroutines` to your game for these functions.

## Common mistakes

- **Forgetting `installFlixelCoroutines()`.** Without it, `launch` creates the coroutine but nothing ever runs it.
- **A coroutine that never pauses.** A `while (true)` loop with no `delay` or `await` freezes the game, because the
  coroutine never gives the frame back.
- **Launching from `update()`.** That starts a new coroutine every frame. Launch once, and loop inside it.
- **Expecting coroutines to survive a state switch.** They are all canceled when the state changes. Launch new ones
  in the next state's `create()`.

## Check your understanding

1. You launched a coroutine but nothing happens. What did you probably forget?
2. Your enemy spawner should stop when the boss appears. How?
3. Where should you put code that must run even when a coroutine is canceled?

(Answers: `installFlixelCoroutines()`; keep the `FlixelJob` from `launch` and call `cancel()` on it; in a `finally`
block, because cancellation throws a `CancellationException` at the paused point.)

**Next:** [Debugging Tools](debugging.md)
