---
name: kotlin-wrapper
description: Write an idiomatic, allocation-free Kotlin wrapper (operator overloads, extension functions, property delegates, or an inline DSL) for a FlixelGDX or FlixelGDX UI Java API, with KDoc and tests. Use when asked to add Kotlin support for a framework API, often after the api-coverage agent reports a gap.
argument-hint: "<Java class or API to wrap>"
---

# Kotlin wrapper

Add Kotlin syntax for `$ARGUMENTS`.

## Steps

1. **Read the Java API** in `../flixelgdx` (or `../flixelgdx-ui`): its signatures, Javadoc, and whether its methods run per frame.
2. **Read `AGENTS.md`** and the existing extensions for the same area (for example `collections/` or `tween/`). Match their file layout and naming: one focused file per framework area, such as `FlixelArrays.kt`.
3. **Pick the right form.**
   - Arithmetic on value types: `operator fun`. Prefer the in-place form (`plusAssign`) for mutable types so game code does not allocate.
   - Builders and configuration: an `inline` DSL with a receiver lambda.
   - Callbacks that model waiting: a `suspend` function in `org.flixelgdx.ktx.async`, using `kotlin.coroutines` only.
   - Simple getters and setters: leave them alone; Kotlin already exposes them as properties.
4. **Keep it allocation-free.** Mark lambda-taking functions `inline`, avoid capturing lambdas, never create iterators or temporary objects, and never use `java.util` collections. No reflection.
5. **Stay a syntax layer.** If the wrapper needs logic the Java API does not have, stop and report that it belongs in the framework instead.
6. **Write KDoc** for each public declaration: one summary sentence, `@param`/`@return`/`@throws`, `[TypeName]` links, American English, and a short usage example on the main entry point.
7. **Add tests** under the module's `src/test/kotlin` that compare the wrapper's result with the plain Java call.
8. **Run the finishing checks** from `AGENTS.md`: `./gradlew test`, `spotlessApply`, `detekt`, and `dokkaGenerate`, one at a time.

Report the new declarations, the files touched, and the check results.
