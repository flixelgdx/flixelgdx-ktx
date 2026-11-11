/// Kotlin operators and destructuring for FlixelGDX vectors, rectangles, and ranges.
///
/// ## Two kinds of operators
///
/// The difference between these matters for performance:
///
/// - Compound operators (`+=`, `-=`, `*=`) change the object in place and allocate nothing. Use
///   these in per-frame code.
/// - Binary operators (`+`, `-`, `*`, and unary `-`) return a new object each time. They read
///   nicely but allocate, so keep them out of update loops.
///
/// ```kotlin
/// // Every frame: no allocations.
/// position += velocity
/// velocity *= 0.98f
///
/// // Once, during setup: allocating is fine.
/// val spawnPoint = origin + offset
/// ```
///
/// Rectangles move by a `Pair` of offsets, and `*` scales every field by a factor:
///
/// ```kotlin
/// hitbox += 4f to 0f
/// val doubled = hitbox * 2f
/// ```
///
/// ## Destructuring
///
/// Vectors and rectangles destructure into their components, without allocating:
///
/// ```kotlin
/// val (x, y) = player.velocity
/// val (left, top, width, height) = hitbox
/// ```
///
/// ## Range easing
///
/// Particle emitter ranges get an `ease` helper that sets the easing curve and returns the range,
/// so it can be chained:
///
/// ```kotlin
/// emitter.alpha.set(1f, 1f, 0f, 0f)
/// emitter.alpha.ease(FlixelEase::quadIn)
/// emitter.scale.ease(FlixelEase::quadOut)
/// ```
package org.flixelgdx.ktx.math;
