# Tweens and Animation: Smooth Motion and Sprite Clips

A tween changes a value smoothly over time, such as sliding a sprite or fading it out. An animation clip flips through
frames of a spritesheet. This guide covers the extension's `tween(...)` builder, chaining tweens with `then { }`, and
the `animations { }` block for registering clips on a sprite.

## Table of contents

1. [Why a tween builder?](#why-a-tween-builder)
2. [Your first tween](#your-first-tween)
3. [Every option](#every-option)
4. [Goals: what the tween changes](#goals-what-the-tween-changes)
5. [Callbacks](#callbacks)
6. [Chaining tweens with then](#chaining-tweens-with-then)
7. [Registering animation clips](#registering-animation-clips)
8. [Common mistakes](#common-mistakes)
9. [Check your understanding](#check-your-understanding)

## Why a tween builder?

In Java, a tween is set up through a `FlixelTweenSettings` object: create it, call a setter for each option, add each
goal, then pass it to `FlixelTween.tween(...)`. The builder puts the options in named parameters with sensible
defaults and the goals in a block, so a tween reads top to bottom like a sentence: "tween this, for this long, with
this ease, toward these values."

For what tweens are and how eases and tween types behave, see the framework's
[Tweens and Timers guide](https://github.com/flixelgdx/flixelgdx/blob/master/handbook/tweens-and-timers.md).

## Your first tween

`tween(...)` is an extension on every object. The object you call it on is the tween's subject, and the block lists
what to change:

```kotlin
sprite.tween(duration = 1.5f, ease = FlixelEase::bounceOut) {
  goal(sprite::getX, 500f, sprite::setX)
  goal(sprite::getY, 300f, sprite::setY)
}
```

The tween is created and started right away, and the call returns the running `FlixelTween`, so you can keep it to
cancel or wait for it later.

## Every option

Every parameter except the goals block has a default:

| Parameter     | Default                  | What it does                                                         |
|---------------|--------------------------|----------------------------------------------------------------------|
| `duration`    | `1f`                     | How long the tween runs, in seconds.                                 |
| `ease`        | `null` (linear)          | The easing curve, such as `FlixelEase::quadOut`.                     |
| `type`        | `FlixelTweenType.ONESHOT`| One-shot, `PERSIST`, `BACKWARD`, `LOOPING`, or `PINGPONG`.           |
| `startDelay`  | `0f`                     | Seconds to wait before starting.                                     |
| `loopDelay`   | `0f`                     | Seconds to wait between loops.                                       |
| `framerate`   | `0f`                     | Steps per second for a choppy, frame-based look; `0` is smooth.      |
| `onStart`     | `null`                   | Runs when the tween starts.                                          |
| `onUpdate`    | `null`                   | Runs on every update.                                                |
| `onComplete`  | `null`                   | Runs when the tween finishes.                                        |

Named arguments let you set only what you need, in any order:

```kotlin
title.tween(duration = 0.8f, type = FlixelTweenType.PINGPONG, ease = FlixelEase::sineInOut) {
  goal(title::getAlpha, 0.3f, title::setAlpha)
}
```

## Goals: what the tween changes

`goal(getter, to, setter)` eases one `Float` property toward `to`:

- The **getter** is read **once**, when the tween begins, to find the starting value.
- The **setter** is called on every update with the new value.
- Function references such as `sprite::getX` and `sprite::setX` fit both. They map straight to the framework's
  primitive `float` interfaces, so updating the tween never boxes a number.

Add as many goals as you need; every goal shares the tween's duration and ease. The block must add at least one goal;
without one, the call throws an `IllegalArgumentException`.

Any `Float` you can read and write can be a goal, not just sprite properties:

```kotlin
class Hud {
  var shake = 0f
}

hud.tween(duration = 0.4f) {
  goal(hud::shake, 0f) { hud.shake = it }
}
```

## Callbacks

`onStart`, `onUpdate`, and `onComplete` receive the tween itself:

```kotlin
coin.tween(duration = 0.3f, onComplete = { coin.kill() }) {
  goal(coin::getY, coin.y - 40f, coin::setY)
  goal(coin::getAlpha, 0f, coin::setAlpha)
}
```

## Chaining tweens with then

`then { }` starts another tween once this one finishes, and returns the first tween so you can keep chaining. The
block must return the next tween, which is exactly what a `tween(...)` call returns:

```kotlin
door.tween(duration = 0.5f) {
  goal(door::getY, 0f, door::setY)
}.then {
  hero.tween(duration = 1f) {
    goal(hero::getX, 320f, hero::setX)
  }
}
```

`then` never runs for `LOOPING` or `PINGPONG` tweens, because they never finish. For longer sequences with waits and
branches, [coroutines](coroutines.md#awaiting-a-tween) usually read better: `tween.await()` pauses until a tween ends.

## Registering animation clips

`sprite.animations { }` opens a block for registering clips on a sprite. It creates the sprite's animation controller
the first time you call it, so sprites without animations never pay for one:

```kotlin
val hero = FlixelSprite(100f, 100f)
hero.loadGraphic(Flixel.files.internal("images/hero.png"), 32, 32)
hero.animations {
  add("idle", 0..3, fps = 6f)
  add("run", 4..11, fps = 12f)
}
hero.animation?.play("run")
```

A sprite's `animation` property is `null` until it has an animation controller, so Kotlin asks for `?.` when you
use it. After `animations { }` has run, the controller exists and the call goes through.

The block has three ways to add a clip:

| Function                                                      | Frames come from                          | Speed given as          |
|---------------------------------------------------------------|-------------------------------------------|-------------------------|
| `add(name, frames, fps = 30f)`                                | An `IntRange` of grid frames, such as `0..5`. | Frames per second.  |
| `addByPrefix(name, prefix, frameRate = 30, loop = true)`      | Atlas frames whose names start with `prefix`. | Whole frames per second. |
| `addFromAtlas(name, atlasFrameIndices, frameDuration = 1f / 30f, loop = true)` | An `IntArray` of atlas frame indices. | Seconds per frame. |

```kotlin
boss.animations {
  addByPrefix("idle", "boss idle ", frameRate = 24)
  addByPrefix("die", "boss die ", frameRate = 24, loop = false)
  addFromAtlas("blink", intArrayOf(10, 11, 10), frameDuration = 1f / 12f)
}
```

Watch the units: `add` and `addByPrefix` take frames per second, while `addFromAtlas` takes seconds per frame
(`1f / fps`), matching the Java method it calls. The block's `controller` property gives you the sprite's
`FlixelAnimationController` if you need anything else from it.

Register clips while setting up (in `create()` or a constructor), not every frame: `add` turns the range into an array.

## Common mistakes

- **Tweening a property that something else also sets every frame.** If your `update()` sets the sprite's X, the
  tween and your code fight, and the sprite jitters. Tween values nothing else touches.
- **Expecting the getter to be read every frame.** The starting value is read once, when the tween begins. Changing
  the property afterwards does not change where the tween is heading or where it came from.
- **Waiting on a looping tween.** `then { }` and `await()` never fire for `LOOPING` or `PINGPONG` tweens.

## Check your understanding

1. You want a sprite to fade to half transparency over two seconds with a smooth ease. Which options do you set?
2. Why does `then { }` need the block to return a tween?
3. You registered `addFromAtlas("walk", frames, frameDuration = 12f)` and the clip barely moves. What went wrong?

(Answers: `duration = 2f`, an `ease` such as `FlixelEase::sineInOut`, and a goal on the alpha toward `0.5f`; because
`then` starts whatever tween the block returns, which is how `then` knows what comes next; `frameDuration` is seconds
per frame, so `12f` means twelve seconds per frame, and it should be `1f / 12f`.)

**Next:** [Math and Colors](math-and-colors.md)
