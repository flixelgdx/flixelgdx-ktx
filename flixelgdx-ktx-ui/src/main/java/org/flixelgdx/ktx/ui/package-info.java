/// Kotlin DSLs for building FlixelGDX UI screens: widget trees, skins, bindings, and animations.
///
/// The UI never reads input on its own. Your game decides when a widget is clicked or a modal is
/// closed, and these helpers only make building and wiring the UI shorter.
///
/// ## Building a widget tree
///
/// `uiDisplay` creates a [FlixelUiDisplay][org.flixelgdx.ui.FlixelUiDisplay] and runs a builder
/// block on its root. Inside the block, each function creates a widget, adds it to the container
/// it is called in, runs its own block, and returns it, so the code has the same shape as the UI:
///
/// ```kotlin
/// ui = uiDisplay(FlixelUiDisplay.createHudCamera(), skin) {
///   vstack(spacing = 8f) {
///     anchor(FlixelAlign.CENTER)
///     label("Main Menu", style = "title")
///     button("Play") { onClick { startGame() } }
///     button("Quit", style = "danger") { onClick { exitGame() } }
///   }
/// }
/// ```
///
/// Use `build { }` to add more widgets to an existing display later. Every builder is `inline`, so
/// the blocks themselves compile away. Only the signal handlers inside them are real objects, and
/// those are created once while the UI is built.
///
/// ## Skins
///
/// `uiSkin { }` builds a [FlixelUiSkin][org.flixelgdx.ui.skin.FlixelUiSkin]. Each style function
/// takes an optional name, which defaults to `"default"`. Wrap backgrounds in `track(...)` so they
/// are destroyed along with the skin:
///
/// ```kotlin
/// val skin = uiSkin {
///   button {
///     up = track(nineSlice(Flixel.files.internal("ui/button.png"), all = 6))
///     fontColor = FlixelColor(FlixelColor.WHITE)
///   }
///   button("danger") { up = track(colorFill(0xAA2222FF)) }
///   label("title") { fontSize = 32 }
/// }
///
/// val danger = skin.get<FlixelUiButtonStyle>("danger")
/// ```
///
/// ## Signals
///
/// Each widget signal has a short registration function, like `onClick`, `onChange`, `onSelect`,
/// `onSubmit`, and `onClose`. Each one returns the handler it registered. Keep it in a variable if
/// you plan to remove it later with `-=`.
///
/// ## Bindings
///
/// `bind(...)` connects a widget to a property in both directions. It shows the property's current
/// value, then writes every change back. Together with the `save()` delegate from
/// `org.flixelgdx.ktx.util`, a settings screen needs no glue code:
///
/// ```kotlin
/// class Settings {
///   var fullscreen by save(false)
///   var playerName by save("Hero")
///   var quality by save(1)
/// }
///
/// checkbox("Fullscreen").bind(settings::fullscreen)
/// textBox(200f).bind(settings::playerName)
/// dropdown(160f) { items("Low", "Medium", "High") }.bind(settings::quality)
/// ```
///
/// Changing the property from code afterwards does not update the widget. Bind again or set the
/// widget directly when you need that.
///
/// ## Containers and text filters
///
/// Containers read like lists (`panel += button`, `panel[0]`, `button in panel`). Walk children
/// with `forEachChild`, which never creates an iterator. Text filters combine with `and`, `or`,
/// and `!`:
///
/// ```kotlin
/// score.filter = FlixelTextFilter.DIGITS or acceptChars("-")
/// ```
///
/// Each combinator creates a small filter object, so build filters once, not every frame.
///
/// ## Widget animations
///
/// `fadeIn`, `fadeOut`, and `fadeTo` tween a widget's alpha. An anchored widget's position belongs
/// to the layout, so tweening its X or Y fights the layout every frame. Move it with
/// `slideAnchor`, which tweens the anchor offsets instead:
///
/// ```kotlin
/// toast.fadeIn(duration = 0.2f)
/// toast.slideAnchor(0f, -40f, duration = 0.3f, ease = FlixelEase::quadOut)
/// ```
package org.flixelgdx.ktx.ui;
