/// A Kotlin builder DSL for creating and chaining [FlixelTween][org.flixelgdx.tween.FlixelTween]s.
///
/// ## Basic example
///
/// Call `tween` on any object, pass the settings as named parameters, and register the property
/// goals inside the block. Every setting has a default, so only the goals are required:
///
/// ```kotlin
/// sprite.tween(duration = 1.5f, ease = FlixelEase::quadOut, onComplete = { it.destroy() }) {
///   goal(sprite::getX, 300f, sprite::setX)
///   goal(sprite::getY, 0f, sprite::setY)
/// }
/// ```
///
/// The getter and setter passed to `goal` map straight to FlixelGDX's primitive `float` goal
/// interfaces, so the tween runs without boxing on every frame.
///
/// ## Chaining tweens
///
/// Use `then` with a trailing lambda to start a follow-up tween when the previous one finishes:
///
/// ```kotlin
/// sprite.tween(duration = 0.5f) { goal(sprite::getX, 300f, sprite::setX) }
///   .then { sprite.tween(duration = 0.5f) { goal(sprite::getY, 200f, sprite::setY) } }
/// ```
///
/// ## Looping
///
/// Pass a [FlixelTweenType][org.flixelgdx.tween.settings.FlixelTweenType] to repeat a tween:
///
/// ```kotlin
/// coin.tween(duration = 0.6f, type = FlixelTweenType.PINGPONG, ease = FlixelEase::sineInOut) {
///   goal(coin::getY, coin.y - 8f, coin::setY)
/// }
/// ```
///
/// To suspend a coroutine until a tween finishes, see `await()` in `org.flixelgdx.ktx.async`.
package org.flixelgdx.ktx.tween;
