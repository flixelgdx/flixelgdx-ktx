# Collections and Pools: Garbage-Free Lists, Maps, and Sets in Kotlin

FlixelGDX has its own collection types (`FlixelArray`, `FlixelMap`, `FlixelIntSet`, and so on) that create no garbage
while you use them. This guide shows how the extension makes them read like Kotlin's own collections: builders such as
`flixelArrayOf(...)`, `+=` and `-=`, `map[key] = value`, `key in map`, destructuring loops, and one-line object pools.

## Table of contents

1. [Why not Kotlin's own collections?](#why-not-kotlins-own-collections)
2. [What already works without the extension](#what-already-works-without-the-extension)
3. [Arrays](#arrays)
4. [Primitive arrays](#primitive-arrays)
5. [Maps](#maps)
6. [Sets](#sets)
7. [Looping without garbage](#looping-without-garbage)
8. [Pools](#pools)
9. [Check your understanding](#check-your-understanding)

## Why not Kotlin's own collections?

`listOf(...)`, `mutableMapOf(...)`, and friends are built on Java's standard collections, which box numbers and create
small objects as you use them. In a game loop that runs 60 times a second, those small objects add up and the garbage
collector pauses the game to clean them. The framework's collections avoid that, and the framework's
[Collections and Pooling guide](https://github.com/flixelgdx/flixelgdx/blob/master/handbook/collections-and-pooling.md)
explains them in depth. The extension only changes how they *look* from Kotlin.

## What already works without the extension

Kotlin maps some Java methods to operators on its own, so these need no import:

- `array[i]` and `array[i] = value` on every array type, because the arrays have `get` and `set` methods.
- `value in array` on the primitive arrays, and `value in set` on every set, because they have `contains` methods.
- `map[key]` on `FlixelMap`, `FlixelIdentityMap`, `FlixelIntMap`, and `FlixelLongMap`.
- `for (value in set)` on `FlixelSet`, because it is `Iterable`.

The extension adds everything else.

## Arrays

```kotlin
val inventory = flixelArrayOf(Sword(), Shield()) // FlixelArray<Item>, like arrayOf(...).
val potion = Potion()

inventory += potion          // Adds to the end.
inventory -= potion          // Removes the first element equal to it.
val hasSword = sword in inventory

if (inventory.isNotEmpty()) {
  val last = inventory[inventory.lastIndex]
}
for (i in inventory.indices) {
  inventory[i].polish()
}
```

| Helper                  | What it does                                                              |
|-------------------------|---------------------------------------------------------------------------|
| `flixelArrayOf(...)`    | Creates a `FlixelArray` holding the given elements.                       |
| `+=` / `-=`             | Adds to the end, or removes the first element that is `equal` to the value. |
| `in`                    | Checks membership with `equals`.                                          |
| `isNotEmpty()`          | `true` when the array has at least one element.                           |
| `lastIndex`, `indices`  | The last valid index (`-1` when empty) and the range of valid indices.    |

`-=` and `in` compare with `equals`, not identity, which is what Kotlin's own lists do too.

## Primitive arrays

Each primitive array type has a builder: `flixelBooleanArrayOf`, `flixelByteArrayOf`, `flixelCharArrayOf`,
`flixelShortArrayOf`, `flixelIntArrayOf`, `flixelLongArrayOf`, and `flixelFloatArrayOf`. They store raw numbers, so
nothing is boxed.

```kotlin
val scores = flixelIntArrayOf(120, 450, 300)
scores += 999
scores -= 120

scores.forEach { score -> total += score } // Inlined: no lambda object, no iterator.
for (score in scores) { /* ... */ }        // Works too, but creates one iterator per loop.
```

They all get `+=`, `-=` (which removes the first matching value), `lastIndex`, `indices`, `forEach { }`, and `for`
loops, with one exception: `FlixelCharArray` has no `-=`, `forEach`, or extension `iterator()`. It is a
`CharSequence`, so Kotlin's own `for (c in chars)` and `c in chars` already work on it.

## Maps

```kotlin
val prices = flixelMapOf("sword" to 100, "shield" to 80) // FlixelMap<String, Int>, like mapOf(...).

prices["potion"] = 25                 // Writes.
val swordPrice = prices["sword"]      // Reads (works through Java interop).
if ("potion" in prices) { /* ... */ } // Checks a key, in constant time.

for ((name, price) in prices) {
  Flixel.info("{} costs {}", name, price)
}
```

| Builder                                 | Creates                 | Keys and values                 |
|-----------------------------------------|-------------------------|---------------------------------|
| `flixelMapOf(...)`                      | `FlixelMap<K, V>`       | Any keys, compared with `equals`. |
| `flixelIdentityMapOf(...)`              | `FlixelIdentityMap<K, V>` | Any keys, compared by identity (`===`). |
| `flixelIntMapOf(...)`                   | `FlixelIntMap<V>`       | `Int` keys, no boxing.          |
| `flixelLongMapOf(...)`                  | `FlixelLongMap<V>`      | `Long` keys, no boxing.         |
| `flixelObjectIntMapOf(...)`             | `FlixelObjectIntMap<K>` | Any keys, `Int` values.         |
| `flixelObjectFloatMapOf(...)`           | `FlixelObjectFloatMap<K>` | Any keys, `Float` values.     |

Every map type gets `map[key] = value`, `key in map`, `isNotEmpty()`, and `for ((key, value) in map)`.

One difference to remember: `FlixelObjectIntMap` and `FlixelObjectFloatMap` do **not** support reading with
`map[key]`, because their getter needs a default for missing keys. Read them with `get(key, default)`:

```kotlin
val kills = flixelObjectIntMapOf("slime" to 3)
kills["bat"] = 1
val slimeKills = kills.get("slime", 0)
```

The `to` pairs in the builders are small objects, so build maps during setup, not every frame. In a destructuring
loop, the map hands out the same entry object on every step, so copy the key and value out (as destructuring does)
rather than keeping the entry itself.

## Sets

```kotlin
val visited = flixelSetOf("forest", "cave") // FlixelSet<String>, like setOf(...).
visited += "castle"
visited -= "cave"
if ("forest" in visited) { /* ... */ }
for (room in visited) { /* ... */ }

val openDoors = flixelIntSetOf(3, 7)
openDoors += 12
openDoors.forEach { id -> openDoor(id) } // Inlined, and reuses the set's own iterator.
```

`flixelSetOf`, `flixelIntSetOf`, and `flixelLongSetOf` create the sets. Every set gets `+=`, `-=`, and
`isNotEmpty()`. The primitive sets (`FlixelIntSet` and `FlixelLongSet`) have no stable order or index, so instead of a
`for` loop they get an inlined `forEach { }`.

## Looping without garbage

Kotlin gives you several ways to loop, and they are not equally cheap:

| Loop                                  | Creates objects?                         | Use it in `update()`? |
|---------------------------------------|------------------------------------------|-----------------------|
| `for (i in array.indices)`            | No.                                      | Yes.                  |
| `array.forEach { }` (primitive arrays and sets) | No. The lambda is inlined.     | Yes.                  |
| `for (value in primitiveArray)`       | One iterator per loop.                   | Avoid.                |
| `for ((key, value) in map)`           | No. Reuses the map's own entry iterator. | Yes, but never nest two loops over the same map. |

## Pools

A pool keeps finished objects so they can be handed out again instead of creating new ones; think of a library that
lends books and takes them back. In Java you create a pool by subclassing `FlixelPool` and overriding `newObject()`.
In Kotlin it is one line:

```kotlin
val bullets = flixelPool { Bullet() }
val sparks = flixelPool(initialCapacity = 64, max = 256) { Spark() }

val bullet = bullets.obtain() // A free bullet, or a new one from the lambda.
// ...
bullets.free(bullet)          // Back to the pool.
```

- `initialCapacity` (default `16`) is the starting size of the pool's storage.
- `max` (default `Int.MAX_VALUE`) is how many free objects the pool keeps; extra ones are dropped.

For an object you only need for a moment, `use { }` borrows it and always returns it, even if the block throws:

```kotlin
val scratchLists = flixelPool { FlixelArray<Enemy>() }

// Later, in update():
scratchLists.use { nearby ->
  nearby.clear()
  findEnemiesNear(player, nearby)
  if (nearby.size > 3) player.panic()
}
```

`use { }` is inlined, so it is safe to call every frame.

## Check your understanding

1. You keep a `FlixelObjectIntMap<String>` of kill counts. Why does `kills["slime"]` not compile?
2. Which is better in `update()`: `for (s in scores)` or `scores.forEach { }` on a `FlixelIntArray`?
3. What happens to an object borrowed with `pool.use { }` if the block throws?

(Answers: that map's getter needs a default, so use `kills.get("slime", 0)`; `forEach`, because it is inlined and
creates no iterator; it is still returned to the pool, because `use` frees it in a `finally` block.)

**Next:** [Signals and Groups](signals-and-groups.md)
