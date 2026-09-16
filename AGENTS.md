# FlixelGDX KTX (project instructions for AI assistants)

---

## Project context

FlixelGDX KTX is the official Kotlin extension layer for [FlixelGDX](https://github.com/flixelgdx/flixelgdx).
It is a **pure syntax layer**: operator overloads, extension functions, property delegates, and DSL builders
that make the Java-based FlixelGDX API feel natural from Kotlin. It contains no runtime logic of its own and
adds no new framework behavior.

This repository has two published modules:

- **`flixelgdx-ktx-core`**: Extensions for collections, tweens, signals, saves, math types, groups, colors,
  and animations.
- **`flixelgdx-ktx-async`**: Coroutine integration; depends on `flixelgdx-ktx-core` and
  `kotlinx-coroutines-core`.

Runtime verification happens in a **separate game project** that consumes the extension via
`publishToMavenLocal`, a composite build, or JitPack.

---

## Collaboration before implementation

Treat the interaction as teamwork, not robotic task execution. Prefer brainstorming when the user's direction
is ambiguous.

- Before implementing anything (planning or coding):

  1. Ask yourself whether the requested design actually fits the KTX extension's purpose. A contribution
     belongs here if it is Kotlin syntax sugar over an existing FlixelGDX API. New framework behavior belongs
     in the base framework instead.
  2. If the change hurts the extension, breaks invariants, or there is clearly a better path, **stop before
     editing files or running commands**. Explain why, suggest alternatives, and ask whether the user still
     wants to proceed.
  3. If they confirm after that discussion, proceed as requested.

- If you are unsure about something, whether that would be for a library, a specific aspect of the codebase,
  or the base framework's API, do **not** assume anything. Verify first. If you are still unsure, ignore it
  and bring it up when you are done with the task.

---

## Explaining things for beginners and contributors

The project welcomes new contributors learning open source and Kotlin.

When explaining code or introducing patterns:

- Explain **why** before **how** (motivation before mechanics).
- Use analogies for complex systems; if the user gave no analogy topic, ask for one they like.
- End **complex** explanations with a short check-in question so you can verify understanding.
- Stay encouraging and professional. Assume intelligence but not deep familiarity with Kotlin idioms or
  FlixelGDX internals.

---

## Code quality (non-negotiables)

### Allocations

**Do not allocate objects in any code path that might be called per frame.** Extension functions on game
objects (`FlixelSprite`, `FlixelGroup`, etc.) are the most likely hot paths. The extension is explicitly
designed to stay allocation-free so it is safe in `update()` and `draw()`.

Prefer:
- `inline` functions for DSL lambdas (avoids the lambda wrapper object).
- Fields over local objects for any reusable temporary state.
- Non-capturing lambdas and direct function references.

### No standard Java collections

Standard Java collection types (`ArrayList`, `HashMap`, `HashSet`, etc.) are banned in both modules for the
same reasons they are banned in the base framework: they allocate too much garbage and consume too much
memory. Use FlixelGDX collection types (`FlixelArray`, `FlixelMap`, `FlixelSet`, and so on) instead.
The only exception is build-time code (Gradle plugins), which does not run at game runtime.

### No reflection

Reflection is banned. It breaks AOT compilers and any platform target that requires ahead-of-time
compilation. If reflection appears to be needed, do not use it; stop and bring it up with the user.

### No deprecated APIs

Do not use deprecated FlixelGDX or Kotlin APIs. If a deprecated API is already in use in the file you are
editing, replace it with the recommended alternative.

---

## Coding style

### Kotlin

- All source is formatted with **ktfmt** (Google style, 2-space indentation). Run
  `./gradlew spotlessApply` before committing. CI rejects unformatted code.
- Target **Kotlin 2.0** language and API version. Do not use features introduced after that version.
- Target **Java 17** bytecode via the JVM toolchain.
- Use `@Suppress` sparingly. If detekt flags something, fix it or add a brief comment explaining why
  the suppression is warranted.
- Prefer `inline` for extension functions that accept lambdas unless there is a specific reason not to.
- Use `operator fun` for operator overloads; use `infix fun` only when the infix form reads clearly.
- Prefer short, focused files grouped by the framework area they extend (for example, all `FlixelArray`
  extensions in `FlixelArrays.kt`).

### KDoc

- Every public extension function, property, or class that is part of the API needs a KDoc comment.
- Start with a single summary sentence, followed by more detail in separate paragraphs when needed.
- Include `@param`, `@return`, and `@throws` tags wherever they apply.
- Reference other types with `[TypeName]` rather than fully qualified names.
- Skip KDoc on trivial one-liners where the name makes the behavior completely obvious.
- Use correct grammar and punctuation. Use **American English**.
- Avoid decorative ASCII art, en dashes, em dashes, emojis, or other non-standard punctuation in prose.
  Use a plain hyphen only for compound adjectives.
- Do not use section comments (like `// ---`). Organize code by structure, not by comment banners.

---

## Architecture and scope

- This extension is a **wrapper layer** only. Do not add new framework behavior; that belongs in
  [flixelgdx-core](https://github.com/flixelgdx/flixelgdx).
- Keep changes minimal. Avoid touching unrelated files unless strictly necessary for the stated task.
- The convention plugin in `build-logic/` applies to every module. Changes there affect the whole project.

---

## Finishing work

Before considering a coding task finished:

1. Run the unit tests: `./gradlew :flixelgdx-ktx-core:test :flixelgdx-ktx-async:test`
2. Apply formatting: `./gradlew spotlessApply`
3. Run static analysis: `./gradlew detekt`
4. Verify KDoc builds: `./gradlew dokkaHtml`

Fix any failures before considering the task complete.

If you are currently on a branch for a pull request, always update the PR description to reflect your
changes after completing a task.

Summarize edits in plain language: what changed, why, and how it fits the extension.

---

## Working with Git and Pull Requests

- Prefer **small, focused commits** as you finish logical slices of work so history stays readable.
- Use **one branch and one pull request** unless the user explicitly asks for more.
- If the user renames a pull request, **do not rename it back**; respect their title.
- Commit titles must be **short and descriptive** (72 characters maximum), **present tense**, and must
  not start with type keywords like `fix:`, `feat:`, or `refactor:`. Examples:
    - "Add operator overloads for FlixelRect"
    - "Fix allocation in tween DSL builder"
    - "Add missing KDoc to FlixelArrays"
- Always pull the latest changes before editing code if on a branch outside of `master`.
- If the current branch is `master`, **create a new branch** off the latest `master` before making changes.
- When you are done with a task (and you have not yet made one), **create a pull request** following the
  [PR template](.github/PULL_REQUEST_TEMPLATE.md) exactly.
- Pull request titles should read as **past tense**, written as if announcing a new update. Examples:
    - "Added Kotlin operator sugar for FlixelGroup to make managing game objects more concise"
    - "Extended the tween DSL with a chained sequence builder for cleaner multi-step animations"
    - "Added coroutine-based signal awaiting to the async module"
- All pull requests must target the **`master`** branch.
- If the user has changes present on the current branch, **do not undo, modify, or touch them**.
