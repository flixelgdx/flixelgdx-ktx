# Project Architecture and Structure

FlixelGDX KTX is a Kotlin extension layer on top of [FlixelGDX](https://github.com/flixelgdx/flixelgdx).
It contains no runtime logic of its own; everything here is idiomatic Kotlin syntax (operator overloads,
extension functions, property delegates, and DSL builders) that wrap the Java API cleanly.

For a full picture of how FlixelGDX itself is organized, see the
[FlixelGDX Architecture document](https://github.com/flixelgdx/flixelgdx/blob/master/ARCHITECTURE.md).

---

## Modules

The project is split into four published modules and one build-only module:

- **`flixelgdx-ktx-core`**: The main extension module. Provides Kotlin operator sugar, DSL builders,
  and property delegates for the core framework API (collections, tweens, signals, saves, math types, groups,
  colors, and animations). Depends on `flixelgdx-core` via `api`, so consumers do not need to declare it
  separately.

- **`flixelgdx-ktx-async`**: The coroutine integration module. Provides a `FlixelCoroutineScope`, the
  `installFlixelCoroutines()` entry point, and `suspend` wrappers so you can `await()` tweens and signals
  or `delay()` for a given number of game-loop seconds. Depends on `flixelgdx-ktx-core` and
  `kotlinx-coroutines-core` via `api`.

- **`flixelgdx-ktx-ui`**: Kotlin sugar for the [FlixelGDX UI](https://github.com/flixelgdx/flixelgdx-ui)
  extension: a widget builder DSL (`uiDisplay { vstack { button("Play") } }`), a skin DSL (`uiSkin { }`),
  two-way property bindings, container operators, text filter combinators, and widget tweens. Depends on
  `flixelgdx-ktx-core` and `flixelgdx-ui` via `api`. Games that do not use the UI extension never pull it in.

- **`flixelgdx-ktx-ui-async`**: Coroutine helpers for UI widgets, such as `awaitClick()` and a
  `dialog(...)` that suspends until the player picks a result. Depends on `flixelgdx-ktx-ui` and
  `flixelgdx-ktx-async` via `api`, so UI users who do not use coroutines do not pull in
  `kotlinx-coroutines-core`.

- **`build-logic/`**: The `flixelgdx.kotlin-library` convention plugin lives here. It wires up the Kotlin
  JVM toolchain (Java 17 target, Kotlin 2.0 API), Spotless formatting with ktfmt (Google style),
  detekt static analysis, Dokka HTML docs, and the Vanniktech Maven publishing pipeline. This module is
  never published; it only exists to keep each subproject's `build.gradle.kts` short.

---

## Key Files

| Path                                                              | Purpose                                                                                                                                                |
|-------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------|
| `settings.gradle.kts`                                             | Declares the submodules, wires `build-logic`, and includes sibling `../flixelgdx` and `../flixelgdx-ui` checkouts as composite builds when they exist. |
| `gradle.properties`                                               | JVM flags, `projectVersion`, `groupId`, and Maven POM metadata.                                                                                        |
| `gradle/libs.versions.toml`                                       | Version catalog: all dependency and plugin versions in one place.                                                                                      |
| `build-logic/src/main/kotlin/flixelgdx.kotlin-library.gradle.kts` | Convention plugin applied to every module.                                                                                                             |

---

## Build System

FlixelGDX KTX uses **Gradle** with **Kotlin DSL**.

### Common Tasks

| Task                                     | Description                                                                        |
|------------------------------------------|------------------------------------------------------------------------------------|
| `./gradlew :flixelgdx-ktx-core:test`     | Run core module unit tests.                                                        |
| `./gradlew :flixelgdx-ktx-async:test`    | Run async module unit tests.                                                       |
| `./gradlew :flixelgdx-ktx-ui:test`       | Run UI module unit tests.                                                          |
| `./gradlew :flixelgdx-ktx-ui-async:test` | Run UI async module unit tests.                                                    |
| `./gradlew test`                         | Run every module's unit tests.                                                     |
| `./gradlew spotlessApply`                | Auto-format all Kotlin source with ktfmt (Google style).                           |
| `./gradlew spotlessCheck`                | Check formatting without modifying files (used in CI).                             |
| `./gradlew detekt`                       | Run detekt static analysis across all modules.                                     |
| `./gradlew dokkaHtml`                    | Generate HTML API docs.                                                            |
| `./gradlew publishToMavenLocal`          | Publish every module to your local Maven repository for testing in a game project. |

---

## GitHub Integration

GitHub configuration, issue templates, and the pull request template are in [`.github/`](./.github/).
