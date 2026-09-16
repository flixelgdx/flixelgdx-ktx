# Contributing to FlixelGDX KTX

We welcome contributions! Whether you are fixing a bug, adding a new Kotlin extension, or improving
documentation, your help is appreciated.

The base [FlixelGDX Contributing guide](https://github.com/flixelgdx/flixelgdx/blob/master/CONTRIBUTING.md)
covers the general workflow, code of conduct stance, AI policy, and pull request process. Please read it
before contributing here. Everything in that guide applies to this repository as well.

This document only covers the parts that are specific to the KTX extension.

---

## What this extension is (and is not)

FlixelGDX KTX is a **pure syntax layer**. It wraps the Java API in idiomatic Kotlin without adding new runtime
behavior. A good contribution:

- Adds Kotlin operator overloads, extension functions, property delegates, or DSL builders that make existing
  FlixelGDX APIs easier or more natural to use from Kotlin.
- Stays **allocation-free** in any code path that could be called per frame. The extension is explicitly designed
  to be safe in `update()` and `draw()`; do not break that guarantee.
- Does **not** duplicate or shadow logic that already lives in `flixelgdx-core`.

If your change adds new runtime behavior or a new framework feature, it belongs in the
[base framework](https://github.com/flixelgdx/flixelgdx) instead.

---

## Java runtime (JDK 17, Eclipse Temurin)

Same requirement as the base framework: use **[Eclipse Temurin 17](https://adoptium.net/temurin/releases/?version=17)**.
See the [Compiling guide](COMPILING.md) for full setup steps.

---

## Automated tests and quality checks

Run all of the following before opening a pull request:

```bash
# Run all unit tests across both modules.
./gradlew :flixelgdx-ktx-core:test :flixelgdx-ktx-async:test

# Auto-format source files.
./gradlew spotlessApply

# Static analysis.
./gradlew detekt

# KDoc generation (catches broken links and malformed tags).
./gradlew dokkaHtml
```

CI runs `spotlessCheck`, `detekt`, both test tasks, and `dokkaHtml`. A PR will not be merged until
all of those pass.

---

## Coding Standards

### Kotlin style

- All source is formatted with **ktfmt** (Google style, 2-space indentation). Run `./gradlew spotlessApply`
  before committing; CI will reject unformatted code.
- Target **Kotlin 2.0** language and API version. Do not use features introduced after that version.
- Target **Java 17** bytecode via the JVM toolchain.
- Use `@Suppress` sparingly. If detekt flags something, either fix it or add a comment explaining why
  the suppression is warranted.

### Allocation rule

**Do not allocate objects in code that a user might call per frame.** Extension functions on game objects
are the most likely candidates. Store temporary objects in fields, use lambda-free overloads where possible,
and prefer `inline` functions for DSL lambdas so no wrapper object is created at the call site.

### No standard Java collections

Standard Java collection types (`ArrayList`, `HashMap`, etc.) are banned from the extension the same as
they are in the base framework. Use FlixelGDX collection types (`FlixelArray`, `FlixelMap`, `FlixelSet`,
and so on) wherever a collection is needed.

### No reflection

Reflection is banned. It breaks AOT compilers and platform targets that require ahead-of-time compilation.
If you believe reflection is genuinely necessary, do not proceed - bring it up in the pull request discussion
first.

### KDoc

- Every public extension function or property that is part of the API needs a KDoc comment.
- Start with a single sentence that describes what the extension does, followed by more detail if needed.
- Include `@param`, `@return`, and `@throws` tags wherever they apply.
- Reference other types with `[TypeName]` rather than fully qualified names.
- Skip KDoc on trivial one-liners where the name makes the behavior obvious.

### File organization

Each file in `org.flixelgdx.ktx.*` wraps one area of the framework. Keep related extensions together in the
same file and follow the established naming pattern: `FlixelArrays.kt` for `FlixelArray` extensions,
`FlixelTweens.kt` for tween extensions, and so on.

---

## Workflow

1. Fork the repository and create a branch off of `master`.
2. Make your changes with small, focused commits.
3. Run the full quality check suite listed above and fix any failures.
4. Open a pull request targeting `master` using the provided PR template.

For large or breaking changes, open a GitHub Discussion first so the approach can be agreed on before
you invest significant time.

---

## Creating a Pull Request

Follow the pull request template exactly. The checklist there mirrors the quality checks described in this
guide. Make sure every item is ticked before requesting a review.
