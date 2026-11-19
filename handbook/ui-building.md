# Building UIs: Widget Trees That Look Like the Menu

A menu is a tree: a screen holds a column, the column holds a title and some buttons. The `flixelgdx-ktx-ui` module
lets the shape of your code match that tree. This guide covers creating a display, every widget builder, layout
helpers, treating containers like lists, and modals.

Everything here comes from `org.flixelgdx.ktx.ui`. For what each widget does and how the UI is drawn, see the
[FlixelGDX UI](https://github.com/flixelgdx/flixelgdx-ui) documentation.

## Table of contents

1. [Why a builder?](#why-a-builder)
2. [Creating a display](#creating-a-display)
3. [Stacks and containers](#stacks-and-containers)
4. [Widget builders](#widget-builders)
5. [Options every builder shares](#options-every-builder-shares)
6. [Keeping references](#keeping-references)
7. [Anchoring and padding](#anchoring-and-padding)
8. [Containers as lists](#containers-as-lists)
9. [Modals](#modals)
10. [The UI never reads input](#the-ui-never-reads-input)
11. [Check your understanding](#check-your-understanding)

## Why a builder?

In Java, every widget is created, configured, and added to its parent by hand:

```java
FlixelUiStack column = new FlixelUiStack(FlixelUiStack.Direction.VERTICAL);
column.setSpacing(8);
column.anchor(FlixelAlign.CENTER, 0, 0);
FlixelUiButton play = new FlixelUiButton("Play");
play.onClick.add(b -> startGame());
column.add(play);
ui.add(column);
```

The builder writes the same tree as nested blocks. Each function creates a widget, adds it to the block it is written
in, runs its own block on it, and returns it:

```kotlin
ui = uiDisplay(FlixelUiDisplay.createHudCamera(), skin) {
  vstack(spacing = 8f) {
    anchor(FlixelAlign.CENTER)
    button("Play") { onClick { startGame() } }
  }
}
```

Every builder is inlined, so the blocks compile away. The only real objects are the widgets themselves and the event
handlers you register, and both are created once while the UI is built.

## Creating a display

`uiDisplay(camera, skin) { }` creates a display and builds its root layer:

```kotlin
class MenuState : FlixelState() {

  private lateinit var ui: FlixelUiDisplay

  override fun create() {
    super.create()
    ui = uiDisplay(FlixelUiDisplay.createHudCamera(), buildSkin()) {
      label("Main Menu", style = "title")
    }
    add(ui)
  }
}
```

- Create the HUD camera in every state's `create()`, because switching states resets the camera list.
- Pass a skin so styles can be looked up while the tree is built. Without one, the display uses an empty skin and every
  widget then needs a style object set directly. See [Skins](ui-skins.md).
- To add more widgets later, call `ui.build { }`, which runs another block on the same root layer.

## Stacks and containers

| Function                                   | Creates                     | Lays out children                     |
|--------------------------------------------|-----------------------------|---------------------------------------|
| `vstack(spacing = 0f, align = null) { }`   | `FlixelUiStack`             | Top to bottom.                        |
| `hstack(spacing = 0f, align = null) { }`   | `FlixelUiStack`             | Left to right.                        |
| `stack(direction, spacing, align) { }`     | `FlixelUiStack`             | In the given `FlixelUiStack.Direction`. |
| `container(width = 0f, height = 0f) { }`   | `FlixelUiContainer`         | Not at all; an invisible group you position yourself. |
| `panel(width = 0f, height = 0f, style) { }`| `FlixelUiPanel`             | Not at all; a container with a styled background. |

`align` takes `FlixelAlign` flags for the cross axis (left for a vertical stack, top for a horizontal one when not
set).

```kotlin
panel(320f, 200f) {
  padding = 12f
  vstack(spacing = 6f, align = FlixelAlign.CENTER) {
    label("Paused")
    hstack(spacing = 8f) {
      button("Resume") { onClick { closePause() } }
      button("Quit") { onClick { quitToMenu() } }
    }
  }
}
```

## Widget builders

| Function                                                        | Creates               |
|-----------------------------------------------------------------|-----------------------|
| `label(text, style) { }`                                        | `FlixelUiLabel`       |
| `button(text, style, tooltip) { }`                              | `FlixelUiButton`      |
| `button(icon, text = null, style, tooltip) { }`                 | `FlixelUiButton` with an icon from a `FlixelFile`. |
| `checkbox(label, checked = false, style, tooltip) { }`          | `FlixelUiCheckbox`    |
| `radio(label, group, style, tooltip) { }`                       | `FlixelUiRadioButton` |
| `dropdown(width, style, tooltip) { }`                           | `FlixelUiDropdown`    |
| `textBox(width, height = null, style, tooltip) { }`             | `FlixelTextBox`; with no height, it is one line tall. |
| `hslider(length, min = 0f, max = 1f, value = min, step = 0f, style, tooltip) { }` | `FlixelUiSlider`, minimum on the left. |
| `vslider(length, min = 0f, max = 1f, value = min, step = 0f, style, tooltip) { }` | `FlixelUiSlider`, minimum at the bottom. |
| `picture(file or frame, width = null, height = null, scaleMode = null, tooltip) { }` | `FlixelUiPicture` |

A few notes:

- **Sliders:** `value` is clamped into the range, and a `step` of `0` means the slider is continuous.
- **Pictures:** a picture uses the image's natural size unless you give both `width` and `height`; then `scaleMode`
  decides how the image fits (fit by default). Pass a `FlixelFile` to load an image, or a `FlixelFrame` to show one
  that is already loaded, such as a region of an atlas. Pictures ignore the pointer.
- **Radio buttons** belong to a `FlixelUiRadioGroup`, which is a plain object rather than a container, so buttons of one
  group can sit in different stacks:

```kotlin
val difficulty = FlixelUiRadioGroup()
hstack(spacing = 12f) {
  radio("Easy", difficulty)
  radio("Normal", difficulty)
  radio("Hard", difficulty)
}
difficulty.select(1)
```

- **Dropdowns** read like a list of strings: `items("Low", "Medium", "High")` adds several, `dropdown += "Ultra"` adds
  one, and `dropdown[0]` reads one back.

```kotlin
dropdown(160f) {
  items("Low", "Medium", "High")
  selectedIndex = 1
}
```

## Options every builder shares

- **`style`** picks a named style from the skin, such as `"title"` or `"danger"`. Leaving it out uses `"default"`.
- **`tooltip`** sets the text shown when the pointer rests on the widget (not on labels, stacks, or panels).
- **The trailing block** configures the widget. Inside it, `this` is the new widget, so you can set its properties and
  register its events directly.

Each widget is added to its parent **before** its block runs. When the parent is already on a display (as it always is
inside `uiDisplay { }` and `build { }`), the widget's style is already resolved inside the block, and calls that need a
display, such as opening a dropdown, already work.

## Keeping references

Every builder returns its widget, so keep any widget you need later in a variable:

```kotlin
lateinit var scoreLabel: FlixelUiLabel

ui = uiDisplay(FlixelUiDisplay.createHudCamera(), skin) {
  scoreLabel = label("Score: 0")
}

// Later:
scoreLabel.text = "Score: $score"
```

Inside nested blocks, you can only add widgets to the block you are in: calling `label(...)` inside a `button { }`
block is a compile error rather than a label quietly added next to the button. When you really mean the outer
container, use a labeled `this`, such as `this@vstack.label(...)`.

## Anchoring and padding

`anchor(align)` pins a widget to a spot inside its parent, such as the center or a corner, and keeps it there when the
parent resizes. The offsets are optional:

```kotlin
vstack { anchor(FlixelAlign.CENTER) }
label("v1.0") { anchor(FlixelAlign.BOTTOM_RIGHT, -8f, -8f) }
```

Containers get a `padding` property that sets all four sides at once, and a `padding(horizontal, vertical)` function:

```kotlin
panel(300f, 200f) {
  padding = 12f          // Every side.
  padding(16f, 8f)       // 16 on the left and right, 8 on the top and bottom.
}
```

Reading `padding` returns the shared value, or `Float.NaN` when the sides differ.

## Containers as lists

A container reads like a list of widgets:

```kotlin
sidebar += closeButton            // Adds it as the last (topmost) child.
sidebar -= closeButton            // Removes it; does nothing if it is not a child.
val first = sidebar[0]            // The child at index 0, in draw order.
val isChild = closeButton in sidebar
val count = sidebar.size          // The number of direct children.

sidebar.forEachChild { child -> child.alpha = 0.5f }
```

`forEachChild { }` walks the children by index and creates no iterator, so it is safe every frame. Do not add or
remove children of the same container inside it.

## Modals

A modal is a pop-up window that sits above the rest of the UI. `ui.modal(width, height) { }` builds one and opens it:

```kotlin
ui.modal(280f, 140f) {
  vstack(spacing = 8f) {
    anchor(FlixelAlign.CENTER)
    label("Saved!")
    button("OK") { onClick { this@modal.close() } }
  }
}
```

- Unlike the other builders, the block runs **before** the modal opens, so handlers you add to its `onOpen` signal in
  the block still see it open.
- By default the modal is destroyed when it closes, since a modal built on the spot is rarely shown twice. Pass
  `destroyOnClose = false` to keep it and reopen it later with `ui.openModal(modal)`.
- To show a modal and wait for the player's answer in one expression, use `dialog(...)` from
  [UI Events, Bindings, and Animation](ui-interaction.md#dialogs-that-return-an-answer).

## The UI never reads input

FlixelGDX UI does not read the mouse, keyboard, touch, or gamepads by itself, and neither does the extension. Your game
decides when a widget is hovered, pressed, or clicked. The UI extension's `FlixelUiPointer` turns a position and a
"down" or "up" into those calls in a few lines:

```kotlin
private lateinit var pointer: FlixelUiPointer

override fun create() {
  super.create()
  // ...build the UI...
  pointer = FlixelUiPointer(ui)
}

override fun update(elapsed: Float) {
  super.update(elapsed)
  pointer.move(Flixel.mouse.getWorldX(ui.camera), Flixel.mouse.getWorldY(ui.camera))
  if (Flixel.mouse.justPressed(FlixelMouseButton.LEFT)) pointer.down()
  if (Flixel.mouse.justReleased(FlixelMouseButton.LEFT)) pointer.up()
}
```

## Check your understanding

1. Inside a `button("Play") { }` block, you call `label("New!")`. What happens, and why?
2. You want to show a confirmation pop-up only once. Do you need `destroyOnClose = false`?
3. Why is the HUD camera created in every state's `create()`?

(Answers: a compile error, because a block can only add to its own widget and a button is not a container; no, the
default destroys it when it closes, which is what you want for a one-off; because switching states resets the camera
list, so a camera from the last state is gone.)

**Next:** [Skins](ui-skins.md)
