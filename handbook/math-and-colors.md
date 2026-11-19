# Math and Colors: Vector Operators, Rectangles, Colors, and Eased Ranges

Game code is full of small math: move this by that, check if a point is inside a box, blend two colors. This guide
covers the operators and builders the extension adds for `FlixelVector`, `FlixelRect`, and `FlixelColor`, and the
`ease(...)` helper for the range types used by particle emitters.

## Table of contents

1. [The one rule: in place versus new](#the-one-rule-in-place-versus-new)
2. [Vectors](#vectors)
3. [Rectangles](#rectangles)
4. [Colors](#colors)
5. [Easing a range](#easing-a-range)
6. [Check your understanding](#check-your-understanding)

## The one rule: in place versus new

Every math operator in the extension falls into one of two families, and the difference matters for performance:

- **Compound assignments** (`+=`, `-=`, `*=`) change the object on the left **in place** and create nothing. Use them
  in `update()`.
- **Plain operators** (`+`, `-`, `*`, unary `-`) return a **brand new object** each time. They read nicely, but
  calling them every frame creates garbage. Use them in setup code, or when you really want a separate copy.

Think of it as editing a document versus printing a fresh copy with the change.

## Vectors

With `org.flixelgdx.ktx.math` imported:

```kotlin
val velocity = FlixelVector(120f, 0f)
val gravity = FlixelVector(0f, 9.8f)

// In place: no garbage, safe every frame.
velocity += gravity
velocity -= wind
velocity *= 0.98f

// New vectors: fine in setup code.
val spawn = origin + FlixelVector(16f, 0f)
val back = -direction
val half = size * 0.5f

// Destructuring: no garbage.
val (vx, vy) = velocity
```

| Operator  | Effect                                         | Creates an object? |
|-----------|------------------------------------------------|--------------------|
| `a += b`  | Adds `b` into `a`.                             | No.                |
| `a -= b`  | Subtracts `b` from `a`.                        | No.                |
| `a *= f`  | Scales `a` by the number `f`.                  | No.                |
| `a + b`, `a - b`, `a * f` | A new vector with the result.  | Yes.               |
| `-a`      | A new vector pointing the other way. Call `a.negate()` to flip in place. | Yes. |
| `val (x, y) = a` | Reads both components.                  | No.                |

The framework also has a vector pool for short-lived vectors; see the framework's
[Math guide](https://github.com/flixelgdx/flixelgdx/blob/master/handbook/math-and-utilities.md).

## Rectangles

A rectangle can be destructured into its four parts, and checked against a point with `in` (that one already works
through the Java `contains` method):

```kotlin
val (x, y, width, height) = room.bounds

if (player.position in room.bounds) {
  enterRoom(room)
}
```

Moving a rectangle uses a pair of `Float` offsets:

```kotlin
camRect += 8f to 0f          // Moves it right by 8 pixels, in place.
val nudged = camRect + (0f to -4f) // A new, moved rectangle.
val doubled = camRect * 2f         // A new rectangle with position and size doubled.
```

`+=` and `-=` move the rectangle in place, but the `8f to 0f` pair on the right is itself a small object. In code that
runs every frame, change `x` and `y` directly instead:

```kotlin
camRect.x += 8f
```

## Colors

With `org.flixelgdx.ktx.util` imported, there are several ways to build a color. Each one creates a new `FlixelColor`,
so build your palette once and keep it in fields:

```kotlin
val coral = rgba(1f, 0.5f, 0.31f)              // Channels from 0 to 1.
val sky = rgba(135, 206, 235)                   // Channels from 0 to 255 (alpha is still 0 to 1).
val ghost = rgba(255, 255, 255, a = 0.4f)
val crimson = hsv(345f, 0.9f, 0.8f)             // Hue in degrees, then saturation and value from 0 to 1.
val magenta = "#ff00ff".toFlixelColor()        // A hex string.
val lime = 0x00FF00FF.toFlixelColor()           // A packed RGBA8888 integer.

val (r, g, b, a) = coral                         // Destructuring: no garbage.
```

The integer forms of `rgba(...)` clamp out-of-range channels instead of throwing. For a packed integer with the top
bit set, such as `0xFF00FFFF`, Kotlin reads the literal as a `Long`, so convert it first:
`0xFF00FFFF.toInt().toFlixelColor()`.

To blend two colors, `lerp` reads like a sentence:

```kotlin
val dusk = FlixelColor.ORANGE lerp FlixelColor.PURPLE at 0.5f
```

`lerp ... at` returns a new color. `t = 0f` gives a copy of the first color, `t = 1f` a copy of the second, and values
outside that range are not clamped. To blend every frame, call the Java `lerp` on a color you own instead, which
changes it in place:

```kotlin
skyColor.set(dayColor).lerp(nightColor, timeOfDay)
```

## Easing a range

Particle emitters describe how a particle changes over its life with range types: `FlixelRangeBounds` (one number,
such as `alpha` or `speed`), `FlixelPointRangeBounds` (two numbers, such as `scale`), and `FlixelColorRangeBounds`
(`color`). Each has an `ease` field that shapes the change from start to end.

Kotlin cannot assign a function reference to that Java field directly, so the extension adds an `ease(...)` function
that does it for you and returns the range for chaining:

```kotlin
emitter.alpha.set(1f, 1f, 0f, 0f)
emitter.alpha.ease(FlixelEase::quadIn)     // From org.flixelgdx.ktx.math.
emitter.speed.ease { t -> t * t }          // Any (Float) -> Float curve works.
emitter.scale.ease(FlixelEase::quadOut)    // From org.flixelgdx.ktx.math.
emitter.color.ease(FlixelEase::sineInOut)  // From org.flixelgdx.ktx.util.
emitter.alpha.ease(null)                   // Back to a straight line.
```

Set the ease once while setting up the emitter. For the emitter itself, see the framework's
[Particles guide](https://github.com/flixelgdx/flixelgdx/blob/master/handbook/particles.md).

## Check your understanding

1. Which line belongs in `update()`: `position = position + velocity` or `position += velocity`?
2. Why does `0xFF00FFFF.toFlixelColor()` not compile?
3. You want particles to fade slowly at first and quickly at the end. What do you call?

(Answers: `position += velocity`, which changes the vector in place, while `+` creates a new one each frame; the literal
is too big for an `Int`, so Kotlin types it as a `Long`, and you need `.toInt()` first; `emitter.alpha.ease(...)` with a
curve that starts slow, such as `FlixelEase::quadIn`.)

**Next:** [Saving with Property Delegates](saving.md)
