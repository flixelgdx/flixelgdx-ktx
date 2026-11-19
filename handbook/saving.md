# Saving with Property Delegates: Settings and Progress as Plain Properties

Most saved data is a handful of values: the volume, the player's name, the best score. This guide shows how the
`save(...)` delegate turns each one into an ordinary Kotlin property that reads from and writes to the game's save
data, and when that data actually reaches the disk.

## Table of contents

1. [Why delegates?](#why-delegates)
2. [Declaring saved properties](#declaring-saved-properties)
3. [Binding the save first](#binding-the-save-first)
4. [When does it reach the disk?](#when-does-it-reach-the-disk)
5. [Keys and renaming](#keys-and-renaming)
6. [Pairing with UI bindings](#pairing-with-ui-bindings)
7. [Check your understanding](#check-your-understanding)

## Why delegates?

Without the extension, every saved value needs two pieces of code that must agree on a key string:

```kotlin
val volume = Flixel.save.getFloat("masterVolume", 1f)
Flixel.save.data.put("masterVolume", 0.5f)
```

A typo in either string silently loses the setting. A delegated property uses its own name as the key, so the key is
written once, by the compiler. Think of it as a labeled jar: you write the label once, and every time you reach for
the jar the label is already right.

## Declaring saved properties

`save(default)` comes from `org.flixelgdx.ktx.util` and works with four types:

```kotlin
class Settings {
  var masterVolume by save(1f)      // Float
  var musicEnabled by save(true)    // Boolean
  var playerName by save("Hero")    // String
  var highScore by save(0)          // Int
}
```

- **Reading** the property calls the matching getter on `Flixel.save` (such as `getFloat(...)`) with the property name
  as the key, and returns the default when the key is missing.
- **Writing** the property stores the value in `Flixel.save.data` under the same key.

The default is never written by itself; it is only what you get back until something else is stored. Each read goes to
`Flixel.save`, so the property always shows the current saved value, even if other code changed it.

## Binding the save first

The delegates use the global `Flixel.save`, so bind it to a save name before any saved property is read or written,
usually once at startup:

```kotlin
Flixel.save.bind("MyGame", "slot1")
```

For slots, defaults, and the rest of the save system, see the framework's
[Saving Progress guide](https://github.com/flixelgdx/flixelgdx/blob/master/handbook/saving.md).

## When does it reach the disk?

**Writing a property never touches the disk.** It only changes the save data held in memory. Call `Flixel.save.flush()`
when you want to keep the changes:

```kotlin
class SettingsState : FlixelState() {

  private val settings = Settings()

  fun onVolumeChanged(value: Float) {
    settings.masterVolume = value // Instant: memory only.
  }

  override fun destroy() {
    Flixel.save.flush() // One disk write when the player leaves the screen.
    super.destroy()
  }
}
```

This is on purpose. Writing to disk takes time, and a slider that saved on every change would stutter the game while
the player drags it. Change as many properties as you like, then flush once at a natural moment: leaving a menu,
finishing a level, or when the game is suspended (`Flixel.Signals.gameSuspended`).

## Keys and renaming

The key is the **property name**, not the class name. That has two consequences:

- **Two properties with the same name share one value**, even in different classes. `var volume by save(1f)` in
  `Settings` and in `AudioState` both read and write the key `"volume"`. Use distinct names for distinct settings.
- **Renaming a property loses its saved value**, because the old key is no longer read. Players' saves still hold
  `"oldName"`, but the code now reads `"newName"`. If you must rename after release, read the old key once with
  `Flixel.save.getFloat("oldName", ...)` and copy it over.

The delegates use no reflection: the property name comes from Kotlin's compile-time metadata, so they work on every
platform.

## Pairing with UI bindings

If your game uses FlixelGDX UI, the `flixelgdx-ktx-ui` module can bind a widget straight to a saved property, so a
settings screen needs no glue code at all:

```kotlin
checkbox("Music").bind(settings::musicEnabled)
hslider(200f).bind(settings::masterVolume)
```

See [UI Events, Bindings, and Animation](ui-interaction.md#two-way-bindings).

## Check your understanding

1. The player changed the volume, then the game crashed before leaving the menu. Was the change saved?
2. Why is saving on every slider change a bad idea?
3. You renamed `var vol by save(1f)` to `var volume by save(1f)`. What happens to players' existing settings?

(Answers: no, writing a property only changes memory, and nothing called `Flixel.save.flush()`; disk writes are slow,
so the game would stutter while the slider moves; they are ignored, because the new name is a new key, and every player
gets the default `1f` until the setting is changed again.)

**Next:** [Coroutines](coroutines.md)
