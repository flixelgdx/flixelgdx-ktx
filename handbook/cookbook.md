# "How Do I...?" Cookbook

Short answers to common questions, grouped by topic. Each answer links to the guide that explains it fully.

## Table of contents

1. [Setup](#setup)
2. [Collections](#collections)
3. [Signals and groups](#signals-and-groups)
4. [Tweens and animation](#tweens-and-animation)
5. [Math and colors](#math-and-colors)
6. [Saving](#saving)
7. [Coroutines](#coroutines)
8. [Debugging](#debugging)
9. [UI](#ui)

## Setup

**...add the extension to my game?**
`implementation("org.flixelgdx:flixelgdx-ktx-core:<flixelgdx-version>")`, plus `flixelgdx-ktx-ui` if you use FlixelGDX
UI. ([Getting Started](getting-started.md#installing-with-gradle))

**...fix "operator does not exist" on `+=`?**
Import the operator from the package of the type you use it on, such as `org.flixelgdx.ktx.collections.plusAssign`.
([Getting Started](getting-started.md#imports))

## Collections

**...create a `FlixelArray` with some items?**
`val items = flixelArrayOf(sword, shield)` ([Collections and Pools](collections.md#arrays))

**...add or remove an item?**
`items += potion` and `items -= potion`.

**...loop over a primitive array every frame?**
`scores.forEach { score -> ... }`, which is inlined and creates nothing. ([Collections and Pools](collections.md#looping-without-garbage))

**...write to a map?**
`prices["potion"] = 25`, and check a key with `"potion" in prices`. ([Collections and Pools](collections.md#maps))

**...loop over a map's keys and values?**
`for ((key, value) in map) { ... }`

**...make an object pool?**
`val bullets = flixelPool { Bullet() }` ([Collections and Pools](collections.md#pools))

**...borrow a pooled object for a moment?**
`pool.use { obj -> ... }`, which returns it to the pool even if the block throws.

## Signals and groups

**...add a listener I can remove later?**
Keep it in a field: `val handler = SignalHandler<Int> { ... }`, then `signal += handler` and `signal -= handler`.
([Signals and Groups](signals-and-groups.md#adding-and-removing-listeners))

**...fire a signal?**
`onHurt(25)`, or `onLevelCleared()` for no data. ([Signals and Groups](signals-and-groups.md#firing-a-signal-like-a-function))

**...add something to a group?**
`enemies += enemy` ([Signals and Groups](signals-and-groups.md#groups-as-lists))

**...loop over a group every frame?**
`for (i in 0 until enemies.size) { val e = enemies[i] ?: continue; ... }` ([Signals and Groups](signals-and-groups.md#looping-over-a-group))

## Tweens and animation

**...move a sprite smoothly?**
`sprite.tween(duration = 1f) { goal(sprite::getX, 300f, sprite::setX) }` ([Tweens and Animation](tweens-and-animation.md#your-first-tween))

**...fade a sprite out and remove it?**
`sprite.tween(duration = 0.3f, onComplete = { sprite.kill() }) { goal(sprite::getAlpha, 0f, sprite::setAlpha) }`

**...use an ease?**
`ease = FlixelEase::quadOut` ([Tweens and Animation](tweens-and-animation.md#every-option))

**...run one tween after another?**
`first.then { secondTween() }`, or `await()` both in a coroutine. ([Tweens and Animation](tweens-and-animation.md#chaining-tweens-with-then))

**...register animation clips?**
`sprite.animations { add("run", 0..5, fps = 12f) }` ([Tweens and Animation](tweens-and-animation.md#registering-animation-clips))

**...use clips from an atlas?**
`sprite.animations { addByPrefix("idle", "hero idle ", frameRate = 24) }`

## Math and colors

**...move a vector without creating garbage?**
`position += velocity` (not `position = position + velocity`). ([Math and Colors](math-and-colors.md#vectors))

**...split a vector or rectangle into parts?**
`val (x, y) = vector` and `val (x, y, w, h) = rect`.

**...make a color from 0 to 255 values?**
`rgba(255, 127, 80)` ([Math and Colors](math-and-colors.md#colors))

**...make a color from a hex string?**
`"#ff7f50".toFlixelColor()`

**...blend two colors?**
`val mid = a lerp b at 0.5f`, or `color.lerp(target, t)` to blend in place every frame.

**...set a particle range's curve?**
`emitter.alpha.ease(FlixelEase::quadIn)` ([Math and Colors](math-and-colors.md#easing-a-range))

## Saving

**...save a setting as a property?**
`var volume by save(1f)` ([Saving](saving.md#declaring-saved-properties))

**...write the settings to disk?**
`Flixel.save.flush()`; writing a delegated property only changes memory. ([Saving](saving.md#when-does-it-reach-the-disk))

## Coroutines

**...turn coroutines on?**
`installFlixelCoroutines()`, once. ([Coroutines](coroutines.md#installing-the-dispatcher))

**...wait a few seconds?**
`launch { delay(2f); doThing() }` ([Coroutines](coroutines.md#waiting-for-time))

**...wait for a tween?**
`tween.await()` ([Coroutines](coroutines.md#awaiting-a-tween))

**...wait for a signal?**
`val value = signal.awaitOnce()` ([Coroutines](coroutines.md#awaiting-a-signal))

**...wait for an animation to finish?**
`sprite.animation?.await("attack")` ([Coroutines](coroutines.md#awaiting-an-animation))

**...repeat something forever until stopped?**
`val job = launch { while (isActive) { spawn(); delay(2f) } }`, then `job.cancel()`. ([Coroutines](coroutines.md#launching-a-coroutine))

**...clean up when a coroutine is canceled?**
Put the cleanup in a `finally` block. ([Coroutines](coroutines.md#canceling))

## Debugging

**...run debug setup only in debug mode?**
`debugOnly { ... }` ([Debugging Tools](debugging.md#grouping-debug-setup-with-debugonly))

**...watch a value?**
`Flixel.debug.watches { float("Speed") { player.speed } }` ([Debugging Tools](debugging.md#watches))

**...add a console command with help text?**
`Flixel.debug.console.command("god", usage = "god [on|off]", description = "...") { args -> ... }`
([Debugging Tools](debugging.md#console-commands))

**...show my class in the inspector?**
`Flixel.debug.inspector.inspect<Player>("Player") { int("Health", Player::health) { p, v -> p.health = v } }`
([Debugging Tools](debugging.md#inspector-sections))

**...build a debugger panel?**
`Flixel.debug.panels.panel("game.tools", "Tools") { button("Heal") { player.heal() } }` ([Debugging Tools](debugging.md#custom-panels))

## UI

**...build a menu?**
`ui = uiDisplay(FlixelUiDisplay.createHudCamera(), skin) { vstack { button("Play") { onClick { play() } } } }`, then
`add(ui)`. ([Building UIs](ui-building.md#creating-a-display))

**...center a group of widgets?**
`vstack { anchor(FlixelAlign.CENTER) }` ([Building UIs](ui-building.md#anchoring-and-padding))

**...declare button and label looks?**
`uiSkin { button { up = track(colorFill(0x444444FF)) }; label("title") { fontSize = 32 } }` ([Skins](ui-skins.md#declaring-styles))

**...use an image as a stretchable background?**
`up = track(nineSlice(Flixel.files.internal("ui/button.png"), all = 6))` ([Skins](ui-skins.md#backgrounds))

**...react to a click?**
`button("Play") { onClick { startGame() } }` ([UI Events](ui-interaction.md#event-handlers))

**...connect a settings widget to a saved value?**
`checkbox("Music").bind(settings::musicEnabled)` ([UI Events](ui-interaction.md#two-way-bindings))

**...allow only digits in a text box?**
`box.filter = FlixelTextFilter.DIGITS` ([UI Events](ui-interaction.md#text-filters))

**...fade a widget in?**
`widget.fadeIn()` ([UI Events](ui-interaction.md#animating-widgets))

**...ask the player a yes-or-no question?**
`launch { val yes = ui.dialog(300f, 160f, dismissed = false) { finish -> ... }; if (yes) ... }`
([UI Events](ui-interaction.md#dialogs-that-return-an-answer))

**...make the UI react to the mouse?**
Feed a `FlixelUiPointer` from your `update()`; the UI never reads input itself. ([Building UIs](ui-building.md#the-ui-never-reads-input))
