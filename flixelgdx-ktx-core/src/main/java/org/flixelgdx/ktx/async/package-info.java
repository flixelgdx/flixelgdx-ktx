/// Coroutine support for writing game sequences top to bottom instead of across callbacks.
///
/// Coroutines here run on the game thread, one dispatcher pass per frame. They are built only on
/// the Kotlin standard library (`kotlin.coroutines`), with no threads, locks, or atomics, so they
/// behave the same on every platform, including web builds. Do not mix them with
/// `kotlinx-coroutines`.
///
/// ## Setup
///
/// Call `installFlixelCoroutines()` once, for example in your game's `create()`. It hooks the
/// dispatcher into the game loop and cancels all running coroutines whenever the state switches,
/// so a coroutine never leaks into the next state. Calling it again does nothing.
///
/// ## Launching and waiting
///
/// Start a coroutine with `launch { }` and pause it with `delay(seconds)`:
///
/// ```kotlin
/// installFlixelCoroutines()
/// launch {
///   showBanner("Get ready!")
///   delay(3f)
///   hideBanner()
/// }
/// ```
///
/// A coroutine that never suspends freezes the whole frame, so loops must call `delay` or another
/// suspending function. Launch coroutines once per sequence (for example in `create()`), not every
/// frame, since each `launch` allocates a coroutine object.
///
/// ## Awaiting framework events
///
/// Tweens, signals, and animations can be awaited directly:
///
/// ```kotlin
/// launch {
///   sprite.tween(duration = 1f) { goal(sprite::getX, 300f, sprite::setX) }.await()
///
///   sprite.animation.play("attack")
///   sprite.animation.await("attack")
///
///   val data = Flixel.Signals.preStateSwitch.awaitOnce()
///   Flixel.info("Switching to: " + data.state())
/// }
/// ```
///
/// The debugger inspector can be awaited too, with `awaitSelection()` or `awaitSelectionOf<T>()`,
/// which is handy for console commands that ask you to pick an object.
///
/// ## Canceling
///
/// `launch` returns a [FlixelJob]. The job is also the receiver of the coroutine body, so the body
/// can check `isActive` or call `cancel()` on itself:
///
/// ```kotlin
/// val spawner = launch {
///   while (isActive) {
///     spawnEnemy()
///     delay(2f)
///   }
/// }
///
/// spawner.cancel()
/// ```
///
/// Canceling throws a `CancellationException` at the coroutine's current suspension point, so
/// `finally` blocks still run. `cancelAllCoroutines()` cancels every running coroutine at once.
///
/// ## Writing your own suspending functions
///
/// Use `suspendCancellable` to wrap any callback-based API. Remove the callback in
/// `invokeOnCancellation` so a canceled coroutine leaves nothing behind:
///
/// ```kotlin
/// suspend fun FlixelSignal<Int>.awaitScore(): Int = suspendCancellable { cont ->
///   val handler = SignalHandler<Int> { cont.resume(it) }
///   addOnce(handler)
///   cont.invokeOnCancellation { remove(handler) }
/// }
/// ```
package org.flixelgdx.ktx.async;
