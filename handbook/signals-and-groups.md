# Signals and Groups: Events and Containers That Read Like Kotlin

Signals announce that something happened; groups hold many game objects at once. Both work from Kotlin without help,
but the extension adds a few operators that make them read like Kotlin's own events and lists. This guide covers both.

## Table of contents

1. [Signals in one minute](#signals-in-one-minute)
2. [Adding and removing listeners](#adding-and-removing-listeners)
3. [Firing a signal like a function](#firing-a-signal-like-a-function)
4. [Signals with no data](#signals-with-no-data)
5. [Groups as lists](#groups-as-lists)
6. [Looping over a group](#looping-over-a-group)
7. [Check your understanding](#check-your-understanding)

## Signals in one minute

A `FlixelSignal<T>` is a list of listeners that all run when the signal is dispatched with a value of type `T`. Think
of a school bell: the bell does not need to know which classrooms are listening. The framework's
[Signals guide](https://github.com/flixelgdx/flixelgdx/blob/master/handbook/signals.md) explains the full model,
including one-time listeners, listener order, and avoiding leaks. Everything there applies here.

Kotlin can already pass a lambda to `add(...)`, because a listener is a single-method interface:

```kotlin
val onHurt = FlixelSignal<Int>()
onHurt.add { health -> healthBar.value = health }
```

## Adding and removing listeners

With `org.flixelgdx.ktx.signal` imported, `+=` and `-=` add and remove permanent listeners:

```kotlin
onHurt += { health -> healthBar.value = health }
```

Removing a listener needs the **same object** you added, so a lambda written inline can never be removed. Keep any
listener you plan to remove in a field, wrapped in `SignalHandler { }` (from `org.flixelgdx.signal.FlixelSignal`) so
the field has the listener type:

```kotlin
private val onSuspend = SignalHandler<Void> { saveProgress() }

override fun create() {
  super.create()
  Flixel.Signals.gameSuspended += onSuspend
}

override fun destroy() {
  Flixel.Signals.gameSuspended -= onSuspend
  super.destroy()
}
```

`addOnce(...)` has no operator form; call it directly for a listener that should run only once.

## Firing a signal like a function

The `invoke` operator lets you dispatch a signal by calling it:

```kotlin
onHurt(25)      // Same as onHurt.dispatch(25).
onLevelCleared() // Same as onLevelCleared.dispatch().
```

## Signals with no data

When there is nothing to send, Java code uses `FlixelSignal<Void>`. From Kotlin, `FlixelSignal<Unit>` reads more
naturally, and both work with `signal()`, which dispatches with no data:

```kotlin
val onLevelCleared = FlixelSignal<Unit>()
onLevelCleared.add { Flixel.switchState { NextLevelState() } }
onLevelCleared()
```

The listener receives `null` as its value in that case, so do not read it. Signals that you wait for with coroutines
work the same way; see [Coroutines](coroutines.md#awaiting-a-signal).

## Groups as lists

Every group type in the framework (including `FlixelState` itself) implements `FlixelGroupable`. With
`org.flixelgdx.ktx.group` imported, a group reads like a Kotlin list:

```kotlin
enemies += Enemy(200f, 100f) // add(...)
enemies -= deadEnemy         // remove(...)
val first = enemies[0]       // The member at index 0.
val slots = enemies.size     // The number of member slots.
```

Two details matter:

- **`-=` shrinks the group.** Removing a member closes the gap, so the members after it move down one index.
- **`size` counts slots, and a slot can be empty.** Groups allow `null` slots, so `size` can include them and
  `group[i]` can return `null`. Check each member before using it.

For background on group types, recycling, and kill versus destroy, see the framework's
[Groups and Recycling guide](https://github.com/flixelgdx/flixelgdx/blob/master/handbook/groups-and-recycling.md).

## Looping over a group

`for (enemy in enemies)` works, but it creates one iterator object per loop. In `update()`, prefer an indexed loop,
which creates nothing:

```kotlin
override fun update(elapsed: Float) {
  super.update(elapsed)
  for (i in 0 until enemies.size) {
    val enemy = enemies[i] ?: continue
    if (enemy.exists) enemy.think(elapsed)
  }
}
```

## Check your understanding

1. Why does `onHurt -= SignalHandler { health -> healthBar.value = health }` not remove the listener you added earlier?
2. Why should a loop over `enemies[i]` check each member for `null`?
3. Which loop belongs in `update()`: `for (e in enemies)` or `for (i in 0 until enemies.size)`?

(Answers: removal is by identity, and that `SignalHandler { }` is a brand new object, so keep the original in a field;
because a group can hold empty slots, and `size` counts them; the indexed loop, since it creates no iterator.)

**Next:** [Tweens and Animation](tweens-and-animation.md)
