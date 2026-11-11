/// Kotlin operators and builders for the FlixelGDX collection types.
///
/// FlixelGDX uses its own collections instead of the standard Java ones, since they create far
/// less garbage. This package makes them read like Kotlin collections.
///
/// ## Arrays, sets, and maps
///
/// Each type gets a `flixel*Of(...)` builder, `+=` and `-=`, the `in` operator, and
/// `map[key] = value`. Indexing with `array[i]` already works through Java interop.
///
/// ```kotlin
/// val names = flixelArrayOf("Ava", "Ben")
/// names += "Cai"
/// if ("Ben" in names) names -= "Ben"
///
/// val ids = flixelIntSetOf(1, 2, 3)
/// ids += 4
///
/// val scores = flixelObjectIntMapOf("Ava" to 10, "Ben" to 7)
/// scores["Cai"] = 12
///
/// val bosses = flixelMapOf(1 to "Slime King", 2 to "Lich")
/// for ((level, boss) in bosses) Flixel.info("Level $level: $boss")
/// ```
///
/// The primitive-value maps (like [FlixelObjectIntMap][org.flixelgdx.collections.FlixelObjectIntMap])
/// do not support `map[key]` reads, since their getter needs a default. Call `get(key, default)` for
/// those.
///
/// ## Iterating without garbage
///
/// The primitive arrays and sets have an inlined `forEach`, which allocates nothing. A `for` loop
/// creates one iterator object per loop, so prefer `forEach` or an indexed loop in per-frame code:
///
/// ```kotlin
/// damage.forEach { amount -> player.health -= amount }
///
/// for (i in enemies.indices) enemies[i].update(elapsed)
/// ```
///
/// ## Pools
///
/// `flixelPool { }` creates a [FlixelPool][org.flixelgdx.collections.FlixelPool] from a factory
/// lambda. `use { }` obtains an object, runs the block, and always frees the object afterwards:
///
/// ```kotlin
/// val bullets = flixelPool(initialCapacity = 64) { Bullet() }
///
/// bullets.use { bullet -> bullet.fire() }
/// ```
package org.flixelgdx.ktx.collections;
