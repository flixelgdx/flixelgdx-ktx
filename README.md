<div align="center">

  # FlixelGDX KTX

  [![Maven Central](https://img.shields.io/maven-central/v/org.flixelgdx/flixelgdx-ktx-core)](https://central.sonatype.com/artifact/org.flixelgdx/flixelgdx-ktx-core)
  [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
  [![FlixelGDX 0.6.2](https://img.shields.io/badge/FlixelGDX-0.6.2-red)](https://kotlinlang.org/)
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

---

## How do I install it?

It's as easy as adding a few lines to your build script or configuration file.

### Gradle version catalog (`libs.versions.toml`)

```toml
[versions]
flixelgdx-ktx = "0.6.2"

[libraries]
flixelgdx-ktx-core = { module = "org.flixelgdx:flixelgdx-ktx-core", version.ref = "flixelgdx-ktx" }
flixelgdx-ktx-async = { module = "org.flixelgdx:flixelgdx-ktx-async", version.ref = "flixelgdx-ktx" }
```

```kotlin
// build.gradle.kts (in your core module of your game).
dependencies {
  implementation(libs.flixelgdx.ktx.core)
  implementation(libs.flixelgdx.ktx.async) // If you want to use coroutines.
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
  implementation("org.flixelgdx:flixelgdx-ktx-core:0.6.2")
  implementation("org.flixelgdx:flixelgdx-ktx-async:0.6.2")  // If you want to use coroutines.
}
```

### Maven

```xml
<dependency>
    <groupId>org.flixelgdx</groupId>
    <artifactId>flixelgdx-ktx-core</artifactId>
    <version>0.6.2</version>
</dependency>

<!-- If you want to use coroutines. -->
<dependency>
    <groupId>org.flixelgdx</groupId>
    <artifactId>flixelgdx-ktx-async</artifactId>
    <version>0.6.2</version>
</dependency>
```

---

## Project navigation

- **[Contributing Guide](CONTRIBUTING.md)**: Coding standards, PR requirements, and how to contribute.
- **[Project Structure](ARCHITECTURE.md)**: The multi-module layout and how Gradle is used.
- **[Compiling & Testing](COMPILING.md)**: How to build the framework and test it as a dependency in your own projects.
- **[Code of Conduct](CODE_OF_CONDUCT.md)**: Rules set in place for a stable open source community.
- **[Project Roles](GOVERNANCE.md)**: How each role for the project operates, including project leaders and maintainers.
