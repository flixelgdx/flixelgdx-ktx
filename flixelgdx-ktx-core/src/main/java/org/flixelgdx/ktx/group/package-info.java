/// Kotlin operators that make FlixelGDX groups read like collections.
///
/// These work on anything that is a
/// [FlixelGroupable][org.flixelgdx.group.FlixelGroupable], which covers every group type in the
/// framework.
///
/// ```kotlin
/// enemies += slime
/// enemies -= slime
///
/// val first = enemies[0]
/// Flixel.info("${enemies.size} enemies left")
/// ```
///
/// ## Iterating in per-frame code
///
/// A `for (enemy in enemies)` loop creates one iterator object per loop. In code that runs every
/// frame, use an indexed loop instead, which allocates nothing:
///
/// ```kotlin
/// for (i in 0 until enemies.size) {
///   enemies[i].chase(player)
/// }
/// ```
package org.flixelgdx.ktx.group;
