/// Operators that make [FlixelSignal][org.flixelgdx.signal.FlixelSignal] read like a Kotlin event.
///
/// ## Adding and removing listeners
///
/// Use `+=` and `-=` instead of `add` and `remove`:
///
/// ```kotlin
/// val onDeath = FlixelSignal<Player>()
///
/// val handler = SignalHandler<Player> { player -> Flixel.info("${player.name} died") }
/// onDeath += handler
/// onDeath -= handler
/// ```
///
/// Keep the handler in a variable when you plan to remove it later. A new lambda written at the
/// removal site is a different object, so removing it does nothing.
///
/// ## Firing a signal
///
/// Call the signal like a function instead of calling `dispatch`:
///
/// ```kotlin
/// onDeath(player)
/// onTick()
/// ```
///
/// To suspend a coroutine until a signal fires, see `awaitOnce()` in `org.flixelgdx.ktx.async`.
package org.flixelgdx.ktx.signal;
