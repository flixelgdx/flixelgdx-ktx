# UI Events, Bindings, and Animation: Making the UI Do Things

A menu that only sits there is a picture. This guide covers reacting to widgets (`onClick { }` and friends), binding
widgets to properties so settings screens need no glue code, filtering what can be typed, animating widgets, and
using coroutines to wait for the player, including dialogs that return an answer.

Event handlers, bindings, filters, and tweens come from `org.flixelgdx.ktx.ui`. The waiting functions come from
`org.flixelgdx.ktx.ui.async` and need [coroutines](coroutines.md) to be installed.

## Table of contents

1. [Event handlers](#event-handlers)
2. [Two-way bindings](#two-way-bindings)
3. [Text filters](#text-filters)
4. [Animating widgets](#animating-widgets)
5. [Waiting for the player](#waiting-for-the-player)
6. [Dialogs that return an answer](#dialogs-that-return-an-answer)
7. [Check your understanding](#check-your-understanding)

## Event handlers

Each widget announces changes through a signal. Inside a builder block, a short function registers a handler on it:

```kotlin
button("Play") {
  onClick { startGame() }
}
checkbox("Music") {
  onChange { box -> music.muted = !box.isChecked }
}
hslider(200f, max = 100f) {
  onChange { slider -> volumeLabel.text = "Volume: ${slider.value.toInt()}" }
}
```

| Widget                 | Function       | Fires when                          | Read                         |
|------------------------|----------------|-------------------------------------|------------------------------|
| `FlixelUiButton`       | `onClick`      | It is clicked.                      |                              |
| `FlixelUiCheckbox`     | `onChange`     | It is checked or unchecked.         | `isChecked`                  |
| `FlixelUiRadioButton`  | `onChange`     | It is selected or deselected.       |                              |
| `FlixelUiRadioGroup`   | `onChange`     | The selection changes.              | `selectedIndex`              |
| `FlixelUiSlider`       | `onChange`     | The value changes.                  | `value`                      |
| `FlixelUiDropdown`     | `onSelect`     | A different item is picked.         | `selectedIndex`, `selectedItem` |
| `FlixelTextBox`        | `onChange`     | The text is edited.                 | `text`                       |
| `FlixelTextBox`        | `onSubmit`     | The player submits it, for example with Enter. | `text`            |
| `FlixelUiModal`        | `onClose`      | It closes.                          |                              |

The handler receives the widget that fired. Each function returns the handler it registered, so keep it when you need
to remove the handler later with `-=` from `org.flixelgdx.ktx.signal`:

```kotlin
val handler = playButton.onClick { startGame() }
// Later:
playButton.onClick -= handler
```

A new lambda written at the removal site is a different object and removes nothing.

## Two-way bindings

A settings screen is mostly "show this value, and write it back when the player changes it." `bind(property)` does
both: it shows the property's current value in the widget (without firing the widget's change signal), then writes
every change back to the property.

```kotlin
class Settings {
  var fullscreen by save(false)
  var playerName by save("Hero")
  var quality by save(1)
  var difficulty by save(0)
  var volume by save(0.8f)
}

val settings = Settings()

ui = uiDisplay(FlixelUiDisplay.createHudCamera(), skin) {
  vstack(spacing = 8f) {
    checkbox("Fullscreen").bind(settings::fullscreen)
    textBox(200f).bind(settings::playerName)
    dropdown(160f) { items("Low", "Medium", "High") }.bind(settings::quality)
    hslider(200f).bind(settings::volume)

    val difficulty = FlixelUiRadioGroup()
    radio("Easy", difficulty)
    radio("Hard", difficulty)
    difficulty.bind(settings::difficulty)
  }
}
```

Combined with the [`save()` delegate](saving.md), every change goes straight into the save data. Flush it when the
player leaves the screen.

| Widget                | Binds to         | Notes                                                              |
|-----------------------|------------------|--------------------------------------------------------------------|
| `FlixelUiCheckbox`    | `Boolean`        |                                                                    |
| `FlixelTextBox`       | `String`         | Each edit copies the text into a new string, so use it for short fields. |
| `FlixelUiDropdown`    | `Int` (the index)| Add the items before binding.                                      |
| `FlixelUiRadioGroup`  | `Int` (the index)| Add the radio buttons before binding. An index outside the group leaves the selection alone. |
| `FlixelUiSlider`      | `Float`          | Set the range before binding. A stored value outside the range is clamped in the slider, but the property keeps the original until the player moves the slider. |

Bindings go one way after the first read: **changing the property from code does not update the widget.** Bind again,
or set the widget directly, when that is needed. Bindings call only the property's getter and setter, with no
reflection.

## Text filters

A text box's `filter` decides which characters can be typed. The UI extension has ready-made ones, such as
`FlixelTextFilter.DIGITS` and `FlixelTextFilter.ALPHANUMERIC`, and the extension adds ways to combine them:

```kotlin
scoreBox.filter = FlixelTextFilter.DIGITS or acceptChars("-")
nameBox.filter = FlixelTextFilter.ALPHANUMERIC and !acceptChars(" ")
```

| Combinator          | Accepts a character when                         |
|---------------------|--------------------------------------------------|
| `a and b`           | Both filters accept it.                          |
| `a or b`            | Either filter accepts it.                        |
| `!a`                | The filter rejects it.                           |
| `acceptChars("-.")` | It is one of the given characters.               |

Each combinator creates one small filter object, so build filters once while building the UI.

## Animating widgets

Widgets are ordinary tween targets, so the [`tween(...)` builder](tweens-and-animation.md) works on them. A few
ready-made tweens cover the common cases:

| Function                                   | What it does                                                       |
|--------------------------------------------|--------------------------------------------------------------------|
| `fadeIn(duration, ease, onComplete)`       | Sets alpha to `0`, then fades to fully opaque.                     |
| `fadeOut(duration, ease, onComplete)`      | Fades from the current alpha to fully transparent.                 |
| `fadeTo(alpha, duration, ease, onComplete)`| Fades from the current alpha to `alpha`.                           |
| `slideAnchor(offsetX, offsetY, duration, ease, onComplete)` | Moves an anchored widget by tweening its anchor offsets. |

`duration` defaults to `0.25f` seconds and `ease` to linear. Each returns the running tween, so you can
[`await()`](coroutines.md#awaiting-a-tween) it.

Two traps:

- **A faded-out widget is still there.** At zero alpha it can still be hovered and clicked. Hide or remove it in
  `onComplete` if it should stop reacting:

```kotlin
toast.fadeOut(onComplete = { toast.isVisible = false })
```

- **Do not tween an anchored widget's X or Y.** The layout owns its position and puts it back every frame, so the
  tween and the layout fight. Use `slideAnchor`, which moves the anchor offsets instead, so the widget keeps following
  its parent while it moves:

```kotlin
panel.anchor(FlixelAlign.RIGHT, 300f, 0f) // Start 300 pixels past the right edge.
panel.slideAnchor(-16f, 0f, duration = 0.3f, ease = FlixelEase::quadOut)
```

## Waiting for the player

With coroutines, a small interaction reads top to bottom instead of being split across handlers. Each function pauses
the coroutine until the widget's signal fires once, then returns the new value:

```kotlin
launch {
  val name = nameBox.awaitSubmit()
  greeting.text = "Hello, $name!"
  continueButton.awaitClick()
  startGame()
}
```

| Function                       | Returns                                        |
|--------------------------------|------------------------------------------------|
| `button.awaitClick()`          | Nothing; resumes on the next click.            |
| `checkbox.awaitChange()`       | Whether it is checked after the change.        |
| `radioGroup.awaitChange()`     | The newly selected index.                      |
| `dropdown.awaitSelect()`       | The newly selected index.                      |
| `textBox.awaitSubmit()`        | A copy of the submitted text.                  |
| `modal.awaitClose()`           | Nothing; returns right away if the modal is not open. |

The value is read at the moment the signal fires, so it is always what the player chose. If the coroutine is canceled
first, the waiting handler is removed from the signal.

## Dialogs that return an answer

`ui.dialog(...)` shows a modal and pauses until it is finished, then returns the result. A confirmation box becomes
one expression:

```kotlin
fun confirmQuit() = launch {
  val quit = ui.dialog(300f, 160f, dismissed = false) { finish ->
    vstack(spacing = 8f) {
      anchor(FlixelAlign.CENTER)
      label("Really quit?")
      hstack(spacing = 8f) {
        button("Yes") { onClick { finish(true) } }
        button("No") { onClick { finish(false) } }
      }
    }
  }
  if (quit) Flixel.quit()
}
```

- The block builds the modal's contents with the usual builders, and receives a `finish` function.
  `finish(value)` closes the modal and makes `dialog` return `value`. Only the first call counts.
- If the modal closes any other way (for example your game calls `ui.closeModal()` when Escape is pressed), `dialog`
  returns `dismissed` instead.
- `style` picks a modal style from the skin, like the other builders.
- The modal is destroyed once it closes. Canceling the coroutine closes it too.

`finish` is a parameter rather than a method on the modal so that you can call it from inside nested blocks, such as
a button's `onClick`, where the builder rules hide the modal's own methods.

The result can be any type, so a dialog can also return a choice:

```kotlin
val slot: Int = ui.dialog(320f, 200f, dismissed = -1) { finish ->
  vstack(spacing = 6f) {
    for (i in 0 until 3) {
      button("Slot ${i + 1}") { onClick { finish(i) } }
    }
  }
}
```

## Check your understanding

1. You bound a checkbox to `settings::fullscreen`, then set `settings.fullscreen = true` from code. Why does the box
   stay unchecked?
2. You faded a button out, but players can still click it. Why, and what is the fix?
3. Your `dialog` returns `dismissed` even though the player clicked "Yes". What is the most likely cause?

(Answers: bindings read the property once and then only write back, so set the widget too or bind again; a widget at
zero alpha still receives interactions, so hide or remove it in `onComplete`; the "Yes" button closes the modal some
other way, such as calling `close()` directly, instead of calling `finish(true)`.)

**Next:** [Videos](video.md)
