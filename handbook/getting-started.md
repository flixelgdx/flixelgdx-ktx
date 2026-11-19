# Getting Started: Installing the Extension and Finding Your Way Around

This guide adds FlixelGDX KTX to a Kotlin game, explains which package holds which feature, and covers the few house
rules that keep Kotlin code as fast as the Java it replaces.

## Table of contents

1. [Requirements](#requirements)
2. [Choosing modules](#choosing-modules)
3. [Installing with Gradle](#installing-with-gradle)
4. [Installing with Maven](#installing-with-maven)
5. [Imports](#imports)
6. [A first taste](#a-first-taste)
7. [The house rules](#the-house-rules)
8. [Check your understanding](#check-your-understanding)

## Requirements

- **Kotlin 2.0 or newer** in the module that holds your game code.
- **Java 25 or newer** to build and run, the same as the framework.
- A FlixelGDX game project. If you do not have one yet, follow the framework's
  [Getting Started guide](https://github.com/flixelgdx/flixelgdx/blob/master/handbook/getting-started.md) first, then
  come back here.

## Choosing modules

The extension ships as two modules. Pick the ones that match what your game uses:

| Module                | Add it when                                 | What it brings in                                     |
|-----------------------|---------------------------------------------|-------------------------------------------------------|
| `flixelgdx-ktx-core`  | Always.                                     | The core extensions, plus FlixelGDX core itself.      |
| `flixelgdx-ktx-ui`    | Your game uses the FlixelGDX UI extension.  | The UI extensions, plus `flixelgdx-ktx-core` and FlixelGDX UI. |

Each module pulls in what it wraps, so you do not need to list `flixelgdx-core` or `flixelgdx-ui` again for the
extension's sake.

## Installing with Gradle

Add the dependencies to the module that holds your game code (usually `core`). Replace `<flixelgdx-version>` with
the release you want; the [README](../README.md) badge shows the current one.

**With a version catalog** (`gradle/libs.versions.toml`):

```toml
[versions]
flixelgdx-ktx = "<flixelgdx-version>"

[libraries]
flixelgdx-ktx-core = { module = "org.flixelgdx:flixelgdx-ktx-core", version.ref = "flixelgdx-ktx" }
flixelgdx-ktx-ui = { module = "org.flixelgdx:flixelgdx-ktx-ui", version.ref = "flixelgdx-ktx" }
```

```kotlin
// core/build.gradle.kts
dependencies {
  implementation(libs.flixelgdx.ktx.core)
  implementation(libs.flixelgdx.ktx.ui) // Only if you use FlixelGDX UI.
}
```

**Directly in the build script:**

```kotlin
// Root build.gradle.kts (or settings.gradle.kts if you manage repositories there).
repositories {
  mavenCentral()
}

// core/build.gradle.kts
dependencies {
  implementation("org.flixelgdx:flixelgdx-ktx-core:<flixelgdx-version>")
  implementation("org.flixelgdx:flixelgdx-ktx-ui:<flixelgdx-version>") // Only if you use FlixelGDX UI.
}
```

## Installing with Maven

```xml
<dependency>
  <groupId>org.flixelgdx</groupId>
  <artifactId>flixelgdx-ktx-core</artifactId>
  <version>FLIXELGDX-VERSION</version>
</dependency>

<!-- Only if you use FlixelGDX UI. -->
<dependency>
  <groupId>org.flixelgdx</groupId>
  <artifactId>flixelgdx-ktx-ui</artifactId>
  <version>FLIXELGDX-VERSION</version>
</dependency>
```

## Imports

Almost everything in the extension is an **extension function** or an **operator**. Kotlin only sees an extension
when it is imported, so the first thing to know is where each feature lives:

| Package                          | Module | What is in it                                                        | Guide |
|----------------------------------|--------|----------------------------------------------------------------------|-------|
| `org.flixelgdx.ktx.collections`  | core   | Array, map, set, and pool builders and operators.                    | [Collections and Pools](collections.md) |
| `org.flixelgdx.ktx.signal`       | core   | `+=`, `-=`, and `invoke` for signals.                                | [Signals and Groups](signals-and-groups.md) |
| `org.flixelgdx.ktx.group`        | core   | `+=`, `-=`, `[i]`, `size`, and `for` loops for groups.               | [Signals and Groups](signals-and-groups.md) |
| `org.flixelgdx.ktx.tween`        | core   | The `tween(...)` builder and `then { }`.                             | [Tweens and Animation](tweens-and-animation.md) |
| `org.flixelgdx.ktx.animation`    | core   | The `animations { }` block for sprites.                              | [Tweens and Animation](tweens-and-animation.md) |
| `org.flixelgdx.ktx.math`         | core   | Vector and rectangle operators, and `ease(...)` for ranges.          | [Math and Colors](math-and-colors.md) |
| `org.flixelgdx.ktx.util`         | core   | Color helpers, `ease(...)` for color ranges, and `save(...)`.        | [Math and Colors](math-and-colors.md), [Saving](saving.md) |
| `org.flixelgdx.ktx.async`        | core   | `launch`, `delay`, `await`, and the rest of the coroutine support.   | [Coroutines](coroutines.md) |
| `org.flixelgdx.ktx.debug`        | core   | `debugOnly { }`, watches, console, inspector, and panel builders.    | [Debugging Tools](debugging.md) |
| `org.flixelgdx.ktx.ui`           | ui     | Widget builders, skins, bindings, filters, and widget tweens.        | [Building UIs](ui-building.md) |
| `org.flixelgdx.ktx.ui.async`     | ui     | `awaitClick()`, `dialog(...)`, and the other widget awaits.          | [UI Events](ui-interaction.md) |

Your IDE offers to import most of these as you type. **Operators are the exception**: when you write `inventory +=
potion` and the operator is not imported, the IDE may only report an error instead of suggesting the fix. If an
operator "does not exist", import it by name:

```kotlin
import org.flixelgdx.ktx.collections.flixelArrayOf
import org.flixelgdx.ktx.collections.minusAssign
import org.flixelgdx.ktx.collections.plusAssign
```

## A first taste

Here is a small state that uses several parts of the extension at once. Each piece has its own guide later on.

```kotlin
class PlayState : FlixelState() {

  private val coins = flixelArrayOf<FlixelSprite>()
  private var bestScore by save(0) // Read from and written to Flixel.save.

  override fun create() {
    super.create()
    installFlixelCoroutines()

    val logo = FlixelSprite(100f, 50f, Flixel.files.internal("images/logo.png"))
    add(logo)

    // Slide the logo down, wait for it, then wait two seconds and fade it out.
    launch {
      logo.tween(duration = 0.5f, ease = FlixelEase::quadOut) {
        goal(logo::getY, 120f, logo::setY)
      }.await()
      delay(2f)
      logo.tween(duration = 0.3f) {
        goal(logo::getAlpha, 0f, logo::setAlpha)
      }
    }
  }

  fun collect(coin: FlixelSprite) {
    coins -= coin
    coin.kill()
  }
}
```

## The house rules

The extension follows the same performance rules as the framework, and it helps to know them when you read the
guides:

- **Nothing that runs every frame should create objects.** Creating objects every frame makes the garbage collector
  run more often, which shows up as small stutters. Most helpers are `inline` and create nothing; the guides say
  clearly when one does (for example `vector + other`, `launch { }`, and the color builders).
- **Register once, use many times.** Listeners, coroutines, tweens, debug watches, and UI trees are built in
  `create()` (or a constructor), not in `update()`.
- **No reflection.** Property references such as `settings::volume` and the `save()` delegate use only Kotlin's own
  compile-time metadata, so they work on every platform, including the web.
- **No `kotlinx-coroutines`.** The coroutine support is built only on the Kotlin standard library. Do not add
  `kotlinx-coroutines` to make `launch` or `delay` work; the extension's own versions already do, and kotlinx does not
  build for the web. See [Coroutines](coroutines.md#why-not-kotlinx-coroutines).

## Check your understanding

1. Your game uses FlixelGDX UI. Which modules do you add?
2. You wrote `enemies += boss` and the compiler says the operator does not exist. What is missing?
3. Why does the handbook keep pointing out which helpers create objects?

(Answers: only `flixelgdx-ktx-ui`, since it brings in `flixelgdx-ktx-core` too, though listing both is harmless; the
import for the operator, in this case `org.flixelgdx.ktx.group.plusAssign`; because creating objects every frame
makes the garbage collector run more, which causes stutters, so those helpers belong in setup code.)

**Next:** [Collections and Pools](collections.md)
