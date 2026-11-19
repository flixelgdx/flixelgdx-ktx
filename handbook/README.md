# The FlixelGDX KTX Handbook

Welcome! This handbook is the guide to FlixelGDX KTX, the official Kotlin extension for FlixelGDX. It explains every
part of the extension in plain language, with short examples you can paste into your game, ordered from what you need
on day one to what is good to know later.

## Table of contents

1. [How to use this handbook](#how-to-use-this-handbook)
2. [What the extension is (and is not)](#what-the-extension-is-and-is-not)
3. [Part 1: Core extensions](#part-1-core-extensions)
4. [Part 2: The UI extensions](#part-2-the-ui-extensions)
5. [Appendices](#appendices)
6. [Other documentation](#other-documentation)

## How to use this handbook

- **Every guide stands on its own.** Each one starts with its own table of contents and links to related guides, so
  you can jump straight to what you need.
- **Guides follow the same shape:** why the feature exists, how to use it with real code, the traps to avoid, and a
  short "Check your understanding" quiz with answers.
- **Code examples are Kotlin and use the real API.** Shorter snippets leave out imports; the
  [Getting Started](getting-started.md#imports) guide lists which package each feature lives in.
- **The [Cookbook](cookbook.md)** answers quick "How do I...?" questions in one or two lines each.
- **This handbook covers the Kotlin layer only.** What a tween, a signal, or a skin *is* comes from the framework and
  the UI extension themselves. When a guide needs that background, it links to the
  [FlixelGDX handbook](https://github.com/flixelgdx/flixelgdx/tree/master/handbook).

## What the extension is (and is not)

FlixelGDX is written in Java. Kotlin can already call every Java API, but some of them read awkwardly from Kotlin:
anonymous subclasses for pools, chains of setters for tweens, `add(...)` calls for every widget in a menu. The
extension is a **pure syntax layer** on top: operators, extension functions, property delegates, builders, and
coroutines that make the same API feel natural.

Two things follow from that:

1. **It adds no new game behavior.** Everything you write with it does exactly what the matching Java call does. If you
   know the Java API, you already know what the Kotlin version does.
2. **It is built for game loops.** Most helpers are `inline` and allocate nothing, so they are safe in `update()`.
   The few that do create objects (such as `vector + other` or `launch { }`) say so, and the guides point them out.

## Part 1: Core extensions

The `flixelgdx-ktx-core` module. Every game that uses the extension has it.

| # | Guide | What you will learn |
|---|-------|---------------------|
| 1 | [Getting Started](getting-started.md) | Installing the modules, the package map, imports, and the house rules. |
| 2 | [Collections and Pools](collections.md) | `flixelArrayOf(...)` and friends, `+=` and `-=`, map indexing, loops, and one-line pools. |
| 3 | [Signals and Groups](signals-and-groups.md) | Adding listeners with `+=`, firing a signal like a function, and treating groups like lists. |
| 4 | [Tweens and Animation](tweens-and-animation.md) | The `tween(...)` builder, chaining with `then`, and the `animations { }` block. |
| 5 | [Math and Colors](math-and-colors.md) | Vector and rectangle operators, destructuring, color builders, and easing ranges. |
| 6 | [Saving with Property Delegates](saving.md) | `var volume by save(1f)`, and when the data actually reaches the disk. |
| 7 | [Coroutines](coroutines.md) | `launch`, `delay`, awaiting tweens, signals, and animations, cancellation, and custom awaits. |
| 8 | [Debugging Tools](debugging.md) | `debugOnly { }`, watches, console commands, inspector sections, and panel builders. |

## Part 2: The UI extensions

The `flixelgdx-ktx-ui` module, for games that use FlixelGDX UI.

| # | Guide | What you will learn |
|---|-------|---------------------|
| 9 | [Building UIs](ui-building.md) | `uiDisplay { }`, stacks, every widget builder, containers as lists, and modals. |
| 10 | [Skins](ui-skins.md) | The `uiSkin { }` block, backgrounds, and type-safe style lookups. |
| 11 | [UI Events, Bindings, and Animation](ui-interaction.md) | Event handlers, two-way bindings, text filters, widget tweens, and dialogs as coroutines. |

## Appendices

| Guide | What it is |
|-------|------------|
| ["How Do I...?" Cookbook](cookbook.md) | Quick answers to common questions, grouped by topic. |

## Other documentation

- **The KDoc.** Every public function in the extension is documented in its source file and in the generated API
  docs. This handbook is the map; the KDoc is the street-level view.
- **[`README.md`](../README.md)**: the project overview and feature tour.
- **[`ARCHITECTURE.md`](../ARCHITECTURE.md)**: the module layout, for contributors.
- **[`COMPILING.md`](../COMPILING.md)** and **[`CONTRIBUTING.md`](../CONTRIBUTING.md)**: building the extension and
  contributing to it.

Have fun making something great!
