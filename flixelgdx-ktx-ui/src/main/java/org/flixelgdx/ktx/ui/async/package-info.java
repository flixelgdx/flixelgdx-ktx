/// Suspending functions that wait for the player to use a UI widget.
///
/// These build on the coroutine support in `org.flixelgdx.ktx.async`, so call
/// `installFlixelCoroutines()` once before using them.
///
/// ## Waiting for input
///
/// Each function suspends until the widget's signal fires once, then resumes with the new value.
/// A short interaction reads top to bottom instead of being split across callbacks:
///
/// ```kotlin
/// launch {
///   val name = nameBox.awaitSubmit()
///   greeting.text = "Hello, $name!"
///   continueButton.awaitClick()
///   startGame()
/// }
/// ```
///
/// The available functions are `awaitClick`, `awaitChange`, `awaitSelect`, `awaitSubmit`, and
/// `awaitClose`. The value is read at the moment the signal fires, so it is always the value the
/// player chose.
///
/// ## Dialogs
///
/// `dialog` shows a modal and suspends until it is finished. The block builds the modal's contents
/// and receives a `finish` function. Calling `finish(value)` closes the modal and returns `value`.
/// If the modal closes any other way, the call returns `dismissed` instead:
///
/// ```kotlin
/// launch {
///   val quit = ui.dialog(300f, 160f, dismissed = false) { finish ->
///     label("Really quit?")
///     hstack(spacing = 8f) {
///       button("Yes") {
///         onClick { finish(true) }
///       }
///       button("No") {
///         onClick { finish(false) }
///       }
///     }
///   }
///   if (quit) exitGame()
/// }
/// ```
///
/// ## Canceling
///
/// Canceling the coroutine removes the waiting handler from the signal, and a dialog closes its
/// modal. Cancel from the game thread, as the KTX dispatcher does.
package org.flixelgdx.ktx.ui.async;
