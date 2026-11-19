# Debugging Tools: Watches, Commands, Inspector Sections, and Panels

The FlixelGDX debugger app shows live values, runs console commands, inspects objects, and hosts custom panels. Its
Java API is built from small listener objects and factory calls. This guide covers the Kotlin layer on top: blocks
that group debug setup, typed watches, console commands with named help text, inspector sections written as lists of
rows, and a builder for whole panels.

For what the debugger is and how to connect it, read the framework's
[Debugging guide](https://github.com/flixelgdx/flixelgdx/blob/master/handbook/debugging.md) first. Everything here
comes from `org.flixelgdx.ktx.debug`.

## Table of contents

1. [Grouping debug setup with debugOnly](#grouping-debug-setup-with-debugonly)
2. [Watches](#watches)
3. [Console commands](#console-commands)
4. [Inspector sections](#inspector-sections)
5. [Reacting to the selection](#reacting-to-the-selection)
6. [Property grids](#property-grids)
7. [Custom panels](#custom-panels)
8. [Tables, trees, and canvases](#tables-trees-and-canvases)
9. [Tones, docks, and toasts](#tones-docks-and-toasts)
10. [Check your understanding](#check-your-understanding)

## Grouping debug setup with debugOnly

`debugOnly { }` runs its block with `Flixel.debug` as the receiver, but only while the game runs in debug mode:

```kotlin
override fun create() {
  super.create()
  debugOnly {
    watches.addFloat("Player X") { player.x }
    console.register("god") { player.invincible = !player.invincible }
  }
}
```

Debugger calls are already safe to leave in a release build, since they register a few small objects and then cost
nothing. `debugOnly` skips even that, and keeps every debug registration of a state in one place. The block is inlined,
so it creates no lambda object.

## Watches

A watch shows a live value in the debugger. Calling the watches object like a function opens a block where each value
type has its own short function:

```kotlin
Flixel.debug.watches {
  float("Player X") { player.x }
  int("Score") { score }
  boolean("Grounded") { player.grounded }
  value("State") { machine.current.name }
}
```

| Function   | Value type          | Notes                                                  |
|------------|---------------------|--------------------------------------------------------|
| `int`, `long`, `float`, `double`, `boolean` | The matching primitive. | No boxing, so reading the watch creates nothing. |
| `value`    | Anything, shown with `toString()`. | Boxes numbers; prefer the typed functions.     |

The getter runs often while the debugger is connected, so keep it cheap. Adding a name that already exists replaces
the old watch. `"Score" in Flixel.debug.watches` checks for a watch, and `Flixel.debug.watches -= "Score"` removes one.

## Console commands

`command(...)` adds a console command with its help text as optional named arguments:

```kotlin
Flixel.debug.console.command("give", usage = "give <item> [count]", description = "Adds items to the inventory.") { args ->
  val item = args[0] ?: return@command
  val count = args.int(1, default = 1)
  inventory.add(item, count)
}
```

The handler receives the words typed after the command name:

| Helper                          | Returns                                                                   |
|---------------------------------|---------------------------------------------------------------------------|
| `args.size`                     | How many words were typed.                                                |
| `args[i]`                       | The word at `i`, or `null` if it is missing, so `?:` can pick a fallback. |
| `args.int(i, default)`, `long`, `float`, `double` | The word as a number, or `default` when it is missing or not a number. |
| `args.boolean(i, default)`      | `true` for `true`, `1`, `yes`, or `on`; `false` for `false`, `0`, `no`, or `off` (any case); otherwise `default`. |

Each default is `0` (or `false`) unless you pass one. Registering a name again replaces the old command,
`"give" in Flixel.debug.console` checks for one, and `Flixel.debug.console -= "give"` removes it.

## Inspector sections

When you select an object in the debugger's inspector, it shows sections of rows describing it. `inspect<T>(...)` adds
a section for every selected object of type `T` (and its subclasses):

```kotlin
Flixel.debug.inspector.inspect<Player>("Player") {
  int("Health", Player::health) { p, v -> p.health = v } // Editable, because it has a setter.
  float("Speed") { it.speed }                              // Read-only.
  enum("State", get = Player::state)
  text("Name") { p, out -> out.concat(p.name) }
  int("Id", once = true, get = Player::id)                 // Read once per selection.
}
```

Each row function comes in two forms. With only a getter, the row is read-only. With a getter and a setter (the
trailing lambda), the row is editable in the app, and edits call the setter. The row types are `int`, `long`,
`float`, `double`, `boolean`, `text`, and `enum` (read-only). Pass `once = true` for values that never change, so they
are read only once per selection.

Text rows append to a reused buffer (`out`) instead of returning a string, so they create no new string each frame.

To describe an object that hangs off the selection, such as the player's weapon, pass `via`. The section is hidden
whenever `via` returns `null`:

```kotlin
Flixel.debug.inspector.inspect("Weapon", via = Player::weapon) {
  int("Ammo", Weapon::ammo) { w, v -> w.ammo = v }
}
```

`unregister<Player>()` removes the section, and `isRegistered<Player>()` checks for one.

## Reacting to the selection

| Helper                         | What it does                                                                  |
|--------------------------------|-------------------------------------------------------------------------------|
| `inspector.selectedAs<T>()`    | The selected object if it is a `T`, otherwise `null`.                         |
| `inspector.onSelect<T> { }`    | Runs the block whenever a `T` is selected. Keep the returned listener to remove it with `removeSelectionListener(...)`. |
| `inspector.awaitSelectionOf<T>()` | Suspends a [coroutine](coroutines.md) until a `T` is selected, then returns it. |
| `inspector.awaitSelection()`   | The same, for any object.                                                     |

The await functions come from `org.flixelgdx.ktx.async`. They make a command that asks you to pick a target read
naturally:

```kotlin
Flixel.debug.console.command("kill", description = "Kills the enemy you pick next.") {
  launch {
    Flixel.info("Pick an enemy to kill.")
    Flixel.debug.inspector.awaitSelectionOf<Enemy>().kill()
  }
}
```

Selecting objects of other types, and clearing the selection, are ignored.

## Property grids

A property grid is a panel widget that shows named values in foldable sections. `group(...)` adds a section with the
same row functions as inspector sections, except that getters take no argument:

```kotlin
val grid = FlixelDebugProperties()
  .group("Enemy AI") {
    int("Active enemies") { enemies.countLiving() }
    float("Aggression", { ai.aggression }) { ai.aggression = it }
    boolean("Paused", { ai.paused }) { ai.paused = it }
  }
  .group("World") {
    double("Time") { world.time }
  }
```

It is called `group` rather than `section` because the Java class already has a `section` method, and Kotlin always
picks a class's own method over an extension with the same name.

## Custom panels

`panels.panel(...)` builds a panel from nested blocks and registers it with the debugger app:

```kotlin
Flixel.debug.panels.panel("game.enemies", "Enemies", dock = FlixelDebugDock.RIGHT) {
  heading("Enemies")
  label { out -> out.concat(enemies.countLiving()).concat(" alive") }
  checkbox("Frozen", { enemies.frozen }) { enemies.frozen = it }
  slider(0.0..2.0, { enemies.speedScale.toDouble() }) { enemies.speedScale = it.toFloat() }
  val wave = progress()
  row {
    button("Spawn wave") { enemies.spawnWave() }
    button("Kill all", confirm = "Kill every enemy?", tone = FlixelDebugTone.ERROR) { enemies.killAll() }
  }
  onUpdate { wave.setValue(enemies.waveProgress.toDouble()) }
}
```

The options of `panel(...)`:

| Parameter            | Default                  | What it does                                                        |
|----------------------|--------------------------|---------------------------------------------------------------------|
| `id`                 | Required.                | A stable id. Start yours with `"game."` so they never clash with built-in panels. |
| `title`              | Required.                | The name on the panel's tab.                                        |
| `dock`               | `FlixelDebugDock.CENTER` | Which side of the app the panel opens on.                           |
| `updatedWhileClosed` | `false`                  | Keep updating while the tab is closed, for example to record graph history. |
| `replace`            | `false`                  | Replace a registered panel with the same id instead of throwing an `IllegalArgumentException`. |

The block's receiver is the panel's root column. The widgets you can add:

| Kind        | Functions                                                                                             |
|-------------|-------------------------------------------------------------------------------------------------------|
| Layout      | `column { }`, `row { }`, `wrap { }`, `grid("...") { }`, `scroll { }`, `expander("Header") { }`, `split { }`, `tabs { page("Title") { } }` |
| Text        | `label("Fixed")`, `label { out -> }` (rebuilt every frame), `heading(...)`, `hint(...)`, `separator()` |
| Input       | `button(text, confirm, tone) { }`, `checkbox(text, get, set)`, `toggle(text, get, set)`, `slider(range, get, set)`, `number(get, set)`, `textBox(placeholder)`, `textBox(placeholder, clearOnSubmit) { text -> }`, `dropdown(items..., get, set)`, `colorPicker(get, set)` |
| Display     | `progress()`, `progress { value }`, `image()`, `graph("Title") { }`, `properties { }`             |
| Data        | `table { }`, `tree { }`, `canvas { }` (see the next section)                                           |

Each layout function adds its widget to the block it is written in, and a block can only add to its own widget: the
compiler rejects a `label(...)` inside a `table { }` that would otherwise land in the surrounding column. Reach an
outer block on purpose with a labeled `this`, such as `this@column`.

Widgets with a getter refresh themselves every frame while the panel is open. For widgets without one, such as a
progress bar, a table, or a graph, push values in `onUpdate { elapsed -> }`, which runs every frame while the panel is
open. A panel has one `onUpdate` callback, so calling it again replaces the earlier one.

Every builder is inlined, and getters, setters, and click handlers each become one listener object while the panel is
built. Nothing is created per frame, as long as your getters create nothing.

For a reusable panel class, extend `FlixelDebugDslPanel` and fill it with `content { }`:

```kotlin
class EnemyPanel(enemies: EnemyGroup) : FlixelDebugDslPanel("game.enemies", "Enemies") {
  init {
    content {
      heading("Enemies")
      button("Kill all") { enemies.killAll() }
    }
    setDock(FlixelDebugDock.RIGHT)
  }
}

Flixel.debug.panels.register(EnemyPanel(enemies))
```

For the widgets themselves and what each one shows, see the framework's
[Custom Debugger Panels guide](https://github.com/flixelgdx/flixelgdx/blob/master/handbook/debugger-panels.md).

## Tables, trees, and canvases

These widgets show data you rebuild yourself, usually in `onUpdate`. Each has a block function that wraps the Java
"begin" and "end" calls, so the closing call can never be forgotten. Rebuilding every frame is cheap: the new data is
compared with the old, and nothing is sent to the app when they match.

```kotlin
Flixel.debug.panels.panel("game.enemy-list", "Enemy list") {
  val list = table {
    column("Name", 0f)
    column("Health", 80f, alignRight = true, mono = true)
  }

  onUpdate {
    list.rows {
      for (i in 0 until enemies.size) {
        val enemy = enemies[i] ?: continue
        val tone = if (enemy.health < 10) FlixelDebugTone.WARNING else FlixelDebugTone.NORMAL
        row(enemy.id, tone) {
          cell(enemy.name)
          cell(enemy.health)
        }
      }
    }
  }
}
```

| Widget  | Block              | Inside it                                                                                  |
|---------|--------------------|--------------------------------------------------------------------------------------------|
| Table   | `table.rows { }`   | `row(id, tone) { cell(...) }`. Columns come from `column(title, width, sortable, alignRight, mono)`. |
| Tree    | `tree.nodes { }`   | `node(id, parent, text) { detail(...); tone(...); badge(text, tone) }`. Use `-1` as the parent of a top-level node, and set the tone before adding badges. |
| Canvas  | `canvas.shapes { }`| `shape(id, x, y, width, height, tone, label, selected, dashed, dim, filled)`. Choose what the person can do with `setInteract(pick, drag, pan, zoom)`. |

Each block returns `true` when the data changed. The ids you pass are what the app reports back when a row, node, or
shape is selected, so use something stable, such as an object id, rather than a position in a list.

## Tones, docks, and toasts

The debugger stores colors ("tones") and dock sides as plain numbers, which makes it easy to pass the wrong one. The
extension gives each its own type, so the compiler catches the mix-up:

- `FlixelDebugTone`: `NORMAL`, `MUTED`, `ACCENT`, `SUCCESS`, `WARNING`, and `ERROR`.
- `FlixelDebugDock`: `CENTER`, `LEFT`, `RIGHT`, and `BOTTOM`.

```kotlin
statusLabel.tone = FlixelDebugTone.SUCCESS             // Changes only the color, keeping other style flags.
myPanel.setDock(FlixelDebugDock.BOTTOM)
Flixel.debug.panels.toast(FlixelDebugTone.WARNING, "Save file is almost full.")
```

Both compile down to a plain `Int`, so they cost nothing at runtime.

## Check your understanding

1. Why prefer `float("Speed") { player.speed }` over `value("Speed") { player.speed }` in a watch?
2. An inspector row for `Health` is read-only in the app, but you want to edit it. What is missing?
3. Your table's rows never change in the app, even though you build them in `onUpdate`. Where should the `row` calls
   be?

(Answers: `float` passes the number without boxing, while `value` boxes it every time the watch is read; a setter, as
the trailing lambda of the row function; inside `table.rows { }`, which wraps the begin and end calls that send the new
rows.)

**Next:** [Building UIs](ui-building.md)
