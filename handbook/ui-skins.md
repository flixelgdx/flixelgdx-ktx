# Skins: Declaring How the UI Looks

A skin is a named collection of styles: what a button looks like, how big a title is, what color a slider's track is.
Widgets refer to styles by name, so you can restyle the whole UI by changing the skin. This guide covers the
`uiSkin { }` block, backgrounds, and type-safe lookups.

Everything here comes from `org.flixelgdx.ktx.ui`.

## Table of contents

1. [Why a skin block?](#why-a-skin-block)
2. [Declaring styles](#declaring-styles)
3. [Backgrounds](#backgrounds)
4. [Who destroys a background?](#who-destroys-a-background)
5. [Reusing a style](#reusing-a-style)
6. [Looking styles up](#looking-styles-up)
7. [Adding styles later](#adding-styles-later)
8. [Check your understanding](#check-your-understanding)

## Why a skin block?

Think of a skin as a wardrobe and each style as a labeled outfit. A widget says "I wear `danger`," and the skin hands
it the matching outfit for its type. Declaring the whole wardrobe in one block keeps every look in one place:

```kotlin
fun buildSkin(): FlixelUiSkin = uiSkin {
  button {
    up = track(nineSlice(Flixel.files.internal("ui/button.png"), all = 6))
    over = track(nineSlice(Flixel.files.internal("ui/button_over.png"), all = 6))
    fontColor = FlixelColor(FlixelColor.WHITE)
  }
  button("danger") {
    up = track(colorFill(0xAA2222FF))
  }
  label {
    fontSize = 16
  }
  label("title") {
    fontSize = 32
  }
  slider {
    track = track(colorFill(0x333333FF))
    thumb = track(colorFill(0xEEEEEEFF))
  }
}
```

Then pass the skin to the display, and pick styles by name in the builders:

```kotlin
ui = uiDisplay(FlixelUiDisplay.createHudCamera(), buildSkin()) {
  label("Main Menu", style = "title")
  button("Delete save", style = "danger")
}
```

## Declaring styles

Inside `uiSkin { }`, each widget type has a function that creates its style, runs your block on it, and stores it:

| Function     | Style type                 | Used by                       |
|--------------|----------------------------|-------------------------------|
| `button`     | `FlixelUiButtonStyle`      | `button(...)`                 |
| `label`      | `FlixelUiLabelStyle`       | `label(...)`                  |
| `panel`      | `FlixelUiPanelStyle`       | `panel(...)`                  |
| `modal`      | `FlixelUiModalStyle`       | `modal(...)` and `dialog(...)`|
| `checkbox`   | `FlixelUiCheckboxStyle`    | `checkbox(...)`               |
| `radio`      | `FlixelUiRadioButtonStyle` | `radio(...)`                  |
| `dropdown`   | `FlixelUiDropdownStyle`    | `dropdown(...)`               |
| `slider`     | `FlixelUiSliderStyle`      | `hslider(...)` and `vslider(...)` |
| `textBox`    | `FlixelTextBoxStyle`       | `textBox(...)`                |
| `tooltip`    | `FlixelUiTooltipStyle`     | Tooltips                      |

Each takes an optional name, `"default"` when left out (the `DEFAULT_STYLE` constant). Widgets use `"default"` until
you give them another style name. A widget looks its style up when it is added to a display, and throws an
`IllegalArgumentException` if the skin has no style of its type with that name. Inside the block, `this` is the style, so you set its fields directly; the fields
themselves are documented in each style class.

One slider style serves both directions: the thumb size is the same either way, and the track thickness is measured
across the slider.

For a style type the table does not list, use the general form, `style(name, styleObject) { }`.

## Backgrounds

Most visual fields of a style hold a background. The extension has short builders for the two common kinds:

| Function                              | Creates                                                      |
|---------------------------------------|--------------------------------------------------------------|
| `nineSlice(file, all)`                | A nine-slice image with the same unstretched border on every side. |
| `nineSlice(file, horizontal, vertical)` | A nine-slice with one border for the left and right, another for the top and bottom. |
| `colorFill(color)`                    | A solid fill from a `FlixelColor`.                           |
| `colorFill(0xRRGGBBAA)`               | A solid fill from a packed color.                            |

A nine-slice is an image cut into a 3 by 3 grid: the corners keep their size, and the edges and middle stretch. That
is how one small button image fits buttons of any size.

`colorFill` takes the packed value as a `Long`, so a literal like `0xAA2222FF` (too big for an `Int`) can be written
as is.

## Who destroys a background?

A nine-slice loads an image, and that image has to be released when the skin is no longer used. `track(...)` hands a
background to the skin, which destroys it when the skin is destroyed. It returns the same background, so you can track
it on the line where you assign it:

```kotlin
button {
  up = track(nineSlice(Flixel.files.internal("ui/button.png"), all = 6))
}
```

Track every background you create inside a skin block, unless you share it with something else that destroys it.

## Reusing a style

Every style function returns the style it stored, so you can keep one and copy fields from it into another:

```kotlin
uiSkin {
  val base = button {
    up = track(nineSlice(Flixel.files.internal("ui/button.png"), all = 6))
    fontSize = 18
  }
  button("big") {
    up = base.up
    fontSize = 28
  }
}
```

Both styles now share one background object. The skin destroys it once, because it was tracked once.

## Looking styles up

Outside the block, the skin has type-safe lookups that take the style type as a type argument:

```kotlin
val danger = skin.get<FlixelUiButtonStyle>("danger")   // Throws if it does not exist.
val hasTitle = skin.has<FlixelUiLabelStyle>("title")
skin.remove<FlixelUiSliderStyle>()                     // Removes the "default" slider style.
skin["fab"] = FlixelUiButtonStyle()                     // Stores a style under a name.
```

Each name defaults to `"default"`. `get` throws an `IllegalArgumentException` when the style is missing, so check with
`has` first if it might be.

## Adding styles later

`skin.styles { }` adds more styles to an existing skin with the same block syntax:

```kotlin
skin.styles {
  button("success") { up = track(colorFill(0x22AA44FF)) }
}
ui.skin = skin // Restyle the widgets that are already on the display.
```

Widgets already on a display do not notice new styles by themselves. Set the skin on the display again to restyle them.

## Check your understanding

1. Building `button("Delete", style = "danger")` throws an `IllegalArgumentException`. What is missing?
2. Why wrap `nineSlice(...)` in `track(...)`?
3. Why does `colorFill` take a `Long`?

(Answers: a button style named `"danger"` in the skin, since a widget looks up its style when it is added to a
display and throws when the skin has none; declare one with `button("danger") { }`, or check with
`skin.has<FlixelUiButtonStyle>("danger")`; so the skin releases the loaded image when it is destroyed; so packed colors
with the top bit set, such as `0xAA2222FF`, can be written without converting them.)

**Next:** [UI Events, Bindings, and Animation](ui-interaction.md)
