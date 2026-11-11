/// Kotlin DSLs for the FlixelGDX debugger: watches, console commands, inspectors, and panels.
///
/// Debugger calls are safe to leave in release builds, since they cost almost nothing there.
/// Wrapping your setup in `debugOnly { }` skips even that and keeps every debug registration of a
/// state in one place. The block's receiver is `Flixel.debug`.
///
/// ```kotlin
/// override fun create() {
///   debugOnly {
///     watches {
///       float("Player X") { player.x }
///       int("Score") { score }
///     }
///   }
/// }
/// ```
///
/// ## Console commands
///
/// Register a command with `command`. Read its arguments with typed getters that take a default:
///
/// ```kotlin
/// Flixel.debug.console.command("god", usage = "god [on|off]", description = "Makes the player invincible.") { args ->
///   player.invincible = args.boolean(0, !player.invincible)
/// }
/// ```
///
/// ## Inspectors
///
/// `inspect<T>` adds a section to the inspector for every selected object of type `T`. A property
/// with a setter is editable, and one without is read-only:
///
/// ```kotlin
/// Flixel.debug.inspector.inspect<Player>("Player") {
///   int("Health", Player::health) { p, v -> p.health = v }
///   float("Speed") { it.speed }
///   enum("State", get = Player::state)
/// }
/// ```
///
/// ## Panels
///
/// `panel` builds a custom debugger panel. Layout helpers (`row`, `column`, `tabs`, `scroll`, and
/// more) nest like a UI tree, and widgets like `button`, `checkbox`, and `slider` take getters and
/// setters directly:
///
/// ```kotlin
/// Flixel.debug.panels.panel("game.enemies", "Enemies", dock = FlixelDebugDock.RIGHT) {
///   heading("Enemies")
///   label { out -> out.concat(enemies.countLiving()).concat(" alive") }
///   checkbox("Frozen", enemies::isFrozen, enemies::setFrozen)
///   row {
///     button("Spawn wave") { enemies.spawnWave() }
///     button("Kill all", confirm = "Kill every enemy?", tone = FlixelDebugTone.ERROR) {
///       enemies.killAll()
///     }
///   }
/// }
/// ```
///
/// For a panel with its own state, subclass `FlixelDebugDslPanel` and call `content { }` in its
/// `init` block.
///
/// ## Tables, trees, and property grids
///
/// Data widgets fill their contents in a block, such as `rows { }` for tables, `nodes { }` for trees,
/// and `group { }` for property grids:
///
/// ```kotlin
/// table.rows {
///   for (enemy in enemies) row(enemy.id) { cell(enemy.name); cell(enemy.health) }
/// }
/// ```
///
/// ## Tones and docks
///
/// `FlixelDebugTone` and `FlixelDebugDock` wrap the debugger's plain `Int` codes in their own types,
/// so passing a dock where a tone is expected no longer compiles. They still compile down to a
/// plain `Int`.
///
/// @see org.flixelgdx.debug.FlixelDebugManager
package org.flixelgdx.ktx.debug;
