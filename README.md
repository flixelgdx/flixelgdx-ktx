<div align="center">

  # FlixelGDX KTX

  [![Maven Central](https://img.shields.io/maven-central/v/org.flixelgdx/flixelgdx-ktx-core)](https://central.sonatype.com/artifact/org.flixelgdx/flixelgdx-ktx-core)
  [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
  [![FlixelGDX 0.6.4](https://img.shields.io/badge/FlixelGDX-0.6.4-red)](https://kotlinlang.org/)
  [![Kotlin 2.0+](https://img.shields.io/badge/Kotlin-2.0%2B-blue)](https://kotlinlang.org/)
  [![Java 17+](https://img.shields.io/badge/Java-17%2B-orange)](https://adoptium.net/temurin/releases?version=17)

  FlixelGDX KTX is a powerful, all-in-one Kotlin extension of the Java-based game framework [FlixelGDX](https://github.com/flixelgdx/flixelgdx/).
  If you like the simplicity of the framework's API, but want to use Kotlin-flavored syntax and features with it, this extension
  is the perfect one for you.

</div>

---

# What does the extension provide?

FlixelGDX KTX provides a multitude of tooling, including syntatic sugar, DSLs, out-of-the-box support for beloved
Kotlin features like coroutines and property delegates, and so much more!

### Property delegates

The extension provides a set of delegates that integrate nicely with the framework's API.

```kotlin
// Save data to disk automatically from easy-to-read properties.
class SettingsState : FlixelState() {

  var masterVolume by save(1f)   // Stored under the key "masterVolume".
  var playerName by save("Hero") // Stored under the key "playerName".

  override fun destroy() {
    super.destroy()
    Flixel.save.flush() // Persist the accumulated changes to disk once.
  }
}
```

### Collections

Every FlixelGDX collection type gets clean, concise syntatic sugar to keep game development quick, easy and fun.

```kotlin
val steak = Steak()
val inventory = flixelArrayOf(Sword(), steak) // Create a new FlixelArray.
inventory += Potion()          // Add a new object to the array.
inventory -= steak             // Remove an object from the array.
val item = inventory[1]        // Access an element by index.
val hasPotion = steak in inventory  // Check if an element exists.

// Loop through a primitive array with an inlined, allocation-free forEach DSL. 
val ids = flixelIntArrayOf(86749, 2844)
ids.forEach {
  Flixel.info(it)
}

// Instantly create a pool with ease.
val bullets = flixelPool { Bullet() }
```

### Tween DSL

The extension provides an extended `tween(...)` function for all objects, keeping code simple and readable. 

```kotlin
// Slide a sprite to (500, 300) over 1.5 seconds with a bounce.
sprite.tween(duration = 1.5f, ease = FlixelEase::bounceOut) {
  goal(sprite::getX, 500f, sprite::setX)
  goal(sprite::getY, 300f, sprite::setY)
}

// Chain tweens together in a clean sequence.
button.tween(duration = 1f) {
  goal(button::getX, 100f, button::setX)
}.then {
  sprite.tween(duration = 2.5f) {
    goal(sprite::getY, 0f, sprite::setY)
  }
}
```

### Coroutines

FlixelGDX KTX provides clean, direct integration with the framework's core API and Kotlin's native coroutine system.

```kotlin
val onDialogClosed = FlixelSignal<Unit>()

override fun create() {
  // Install the extension's coroutine system once in your game.
  installFlixelCoroutines()

  // Then use them anywhere in your game.
  launch {
    delay(1f)                  // Wait 1 real game-loop second.
    introTween.await()         // Wait until the tween finishes
    onDialogClosed.awaitOnce() // Wait for the player to dismiss the dialog.
    Flixel.switchState { PlayState() }
  }
}
```

### UI toolkit

If your game uses [FlixelGDX UI](https://github.com/flixelgdx/flixelgdx-ui), the `flixelgdx-ktx-ui` module lets the
shape of your code match the shape of your menus.

```kotlin
// Declare your looks once.
val skin = uiSkin {
  button {
    up = track(nineSlice(Flixel.files.internal("ui/button.png"), all = 6)) 
  }
  button("danger") {
    up = track(colorFill(0xAA2222FF))
  }
  label("title") {
    fontSize = 32
  }
}

// Then build the UI as a tree. Each widget is added to the block it is written in.
ui = uiDisplay(FlixelUiDisplay.createHudCamera(), skin) {
  vstack(spacing = 8f) {
    anchor(FlixelAlign.CENTER)
    label("Main Menu", style = "title")
    button("Play", tooltip = "Start a new run") {
      onClick {
        startGame()
      }
    }
    checkbox("Fullscreen").bind(settings::fullscreen) // Two-way binding, works with save().
    dropdown(160f) {
      items("Low", "Medium", "High") 
    }.bind(settings::quality)
    button("Quit", style = "danger") {
      onClick {
        confirmQuit()
      }
    }
  }
}
```

With `flixelgdx-ktx-ui-async`, a dialog is just a function that returns the player's answer:

```kotlin
fun confirmQuit() = launch {
  val quit = ui.dialog(300f, 160f, dismissed = false) { finish ->
    label("Really quit?")
    hstack(spacing = 8f) {
      button("Yes") { onClick { finish(true) } }
      button("No") { onClick { finish(false) } }
    }
  }
  if (quit) Flixel.quit()
}
```

Just like the UI extension itself, none of this reads input: your game still decides when a widget is hovered or
clicked (the UI extension's `FlixelUiPointer` makes that a few lines).

---

## How do I install it?

It's as easy as adding a few lines to your build script or configuration file.

### Gradle version catalog (`libs.versions.toml`)

```toml
[versions]
flixelgdx-ktx = "<flixelgdx-version>"

[libraries]
flixelgdx-ktx-core = { module = "org.flixelgdx:flixelgdx-ktx-core", version.ref = "flixelgdx-ktx" }
flixelgdx-ktx-async = { module = "org.flixelgdx:flixelgdx-ktx-async", version.ref = "flixelgdx-ktx" }
flixelgdx-ktx-ui = { module = "org.flixelgdx:flixelgdx-ktx-ui", version.ref = "flixelgdx-ktx" }
flixelgdx-ktx-ui-async = { module = "org.flixelgdx:flixelgdx-ktx-ui-async", version.ref = "flixelgdx-ktx" }
```

```kotlin
// build.gradle.kts (in your core module of your game).
dependencies {
  implementation(libs.flixelgdx.ktx.core)
  implementation(libs.flixelgdx.ktx.async) // If you want to use coroutines.
  implementation(libs.flixelgdx.ktx.ui) // If you use FlixelGDX UI.
  implementation(libs.flixelgdx.ktx.ui.async) // If you use FlixelGDX UI with coroutines.
}
```

### Gradle Kotlin DSL (direct)

```kotlin
// In your root build.gradle.kts:
repositories {
  mavenCentral()
}

// In your game's core build.gradle.kts script:
dependencies {
  implementation("org.flixelgdx:flixelgdx-ktx-core:<flixelgdx-version>")
  implementation("org.flixelgdx:flixelgdx-ktx-async:<flixelgdx-version>")  // If you want to use coroutines.
  implementation("org.flixelgdx:flixelgdx-ktx-ui:<flixelgdx-version>")  // If you use FlixelGDX UI.
  implementation("org.flixelgdx:flixelgdx-ktx-ui-async:<flixelgdx-version>")  // If you use FlixelGDX UI with coroutines.
}
```

### Maven

```xml
<dependency>
    <groupId>org.flixelgdx</groupId>
    <artifactId>flixelgdx-ktx-core</artifactId>
    <version>FLIXELGDX-VERSION</version>
</dependency>

<!-- If you want to use coroutines. -->
<dependency>
    <groupId>org.flixelgdx</groupId>
    <artifactId>flixelgdx-ktx-async</artifactId>
    <version>FLIXELGDX-VERSION</version>
</dependency>

<!-- If you use FlixelGDX UI (and flixelgdx-ktx-ui-async for UI with coroutines). -->
<dependency>
    <groupId>org.flixelgdx</groupId>
    <artifactId>flixelgdx-ktx-ui</artifactId>
    <version>FLIXELGDX-VERSION</version>
</dependency>
```

---

## Project navigation

- **[Contributing Guide](CONTRIBUTING.md)**: Coding standards, PR requirements, and how to contribute.
- **[Project Structure](ARCHITECTURE.md)**: The multi-module layout and how Gradle is used.
- **[Compiling & Testing](COMPILING.md)**: How to build the framework and test it as a dependency in your own projects.
- **[Code of Conduct](CODE_OF_CONDUCT.md)**: Rules set in place for a stable open source community.
- **[Project Roles](GOVERNANCE.md)**: How each role for the project operates, including project leaders and maintainers.
