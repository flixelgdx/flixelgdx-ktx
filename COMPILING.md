# Compiling & Testing

FlixelGDX KTX is a library, not a standalone application, so it cannot be run by itself. Testing your changes means
consuming the extension in a separate FlixelGDX game project alongside the base framework.

For the full environment setup (Git, JDK 17 via Eclipse Temurin, IntelliJ IDEA, and how to create a test project),
see the [FlixelGDX Compiling guide](https://github.com/flixelgdx/flixelgdx/blob/master/COMPILING.md).
Everything in that guide applies here as well. This document only covers the parts that are specific to the KTX
extension.

---

## Prerequisites

The requirements are identical to those of the base framework:

- **JDK 17** (Eclipse Temurin recommended)
- **Git**
- **IntelliJ IDEA** (heavily recommended)

If you have already set up FlixelGDX locally, you are ready to go.

---

## Getting the source

```bash
git clone https://github.com/flixelgdx/flixelgdx-ktx.git
cd flixelgdx-ktx
```

If you are contributing, fork the repo first and clone your fork instead.

---

## Building

The Gradle wrapper handles everything. Run the following from the repo root to confirm the build is clean:

```bash
./gradlew build
```

This compiles every module, runs all unit tests, and applies formatting and static analysis checks.

### Building against local framework and UI checkouts automatically

You do not need any extra flag to build against sibling clones of the framework or the UI extension.
[`settings.gradle.kts`](settings.gradle.kts) checks whether `../flixelgdx` and `../flixelgdx-ui` exist next to
this repository and, for each one that does, includes it as a composite build automatically:

```kotlin
if (file("../flixelgdx").isDirectory) {
  includeBuild("../flixelgdx")
}

if (file("../flixelgdx-ui").isDirectory) {
  includeBuild("../flixelgdx-ui")
}
```

This substitutes the published artifacts (`org.flixelgdx:flixelgdx-core`, `flixelgdx-ui`, and `flixelgdx-jvm` for
tests) with your local checkouts by module coordinates, so the `flixelgdx` version number in the catalog does not
need to match while the sibling checkouts are present. Any change in the framework or the UI extension is picked up
on the next build with no republishing, which is handy when a KTX feature needs a change on the Java side too.
Clone either repository next to this one to opt in:

```bash
cd ..
git clone https://github.com/flixelgdx/flixelgdx.git
git clone https://github.com/flixelgdx/flixelgdx-ui.git
cd flixelgdx-ktx
```

Remove or rename a sibling directory to go back to resolving that artifact from Maven Central.

---

## Running the unit tests

Unit tests live inside each module rather than a shared test module. Run them individually or together:

```bash
# Core extensions only.
./gradlew :flixelgdx-ktx-core:test

# Coroutine extensions only.
./gradlew :flixelgdx-ktx-async:test

# UI extensions and UI coroutine helpers.
./gradlew :flixelgdx-ktx-ui:test :flixelgdx-ktx-ui-async:test

# Every module at once.
./gradlew test
```

---

## Code quality checks

Before submitting a pull request, make sure all of the following pass:

```bash
# Auto-format Kotlin source files (ktfmt, Google style).
./gradlew spotlessApply

# Verify formatting without changing files.
./gradlew spotlessCheck

# Run detekt static analysis.
./gradlew detekt

# Generate Dokka HTML docs to catch broken KDoc links or malformed tags.
./gradlew dokkaHtml
```

---

## Testing with a game project

Because the extension only provides syntax sugar on top of `flixelgdx-core`, the most valuable runtime tests happen
inside an actual game project. There are two ways to consume your local build.

### Method 1: `publishToMavenLocal`

1. Publish every module to your local Maven repository:
   ```bash
   ./gradlew publishToMavenLocal
   ```
2. In your game project's `settings.gradle.kts`, add `mavenLocal()` before `mavenCentral()` in the
   `dependencyResolutionManagement` block.
3. Your game can then depend on the local version:
   ```kotlin
   implementation("org.flixelgdx:flixelgdx-ktx-core:0.6.2")
   ```

You must re-publish after every change. Use Method 2 to avoid that.

### Method 2: Composite build (recommended)

1. In your game project's `settings.gradle.kts`, add an `includeBuild(...)` declaration pointing to
   your local KTX clone:
   ```kotlin
   includeBuild("/path/to/flixelgdx-ktx")
   ```
   On Windows, use forward slashes: `C:/dev/flixelgdx-ktx`.
2. Keep your existing dependency declarations as-is. Gradle substitutes them automatically with the
   local source.
3. Refresh Gradle in your IDE and changes to the KTX source are picked up on the next build without
   re-publishing.

> [!TIP]
> If your game also depends on a local clone of `flixelgdx-core`, you can chain both `includeBuild` declarations
> and develop across all three repos simultaneously.
