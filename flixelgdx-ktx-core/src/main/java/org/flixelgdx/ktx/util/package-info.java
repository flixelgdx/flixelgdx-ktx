/// Kotlin helpers for colors and save data.
///
/// ## Colors
///
/// Build colors from RGBA floats, RGBA bytes, HSV, a packed `Int`, or a hex string:
///
/// ```kotlin
/// val coral = rgba(255, 127, 80)
/// val glass = rgba(0f, 0f, 1f, a = 0.5f)
/// val crimson = hsv(345f, 0.9f, 0.8f)
/// val gold = 0xFFD700FF.toInt().toFlixelColor()
/// val sky = "#87ceeb".toFlixelColor()
/// ```
///
/// Blend two colors with `lerp ... at`, and destructure a color into its channels:
///
/// ```kotlin
/// val mid = FlixelColor.RED lerp FlixelColor.BLUE at 0.5f
/// val (r, g, b, a) = mid
/// ```
///
/// Each of these returns a new color, so create colors during setup rather than every frame.
///
/// ## Save data
///
/// The `save(default)` delegate binds a property to the global
/// [FlixelSave][org.flixelgdx.save.FlixelSave]. The property name becomes the save key, so no string
/// keys are needed:
///
/// ```kotlin
/// Flixel.save.bind("MyGame", "slot1")
///
/// var masterVolume: Float by save(1.0f)
/// var musicEnabled: Boolean by save(true)
/// var playerName: String by save("Player")
///
/// masterVolume = 0.5f
/// Flixel.save.flush()
/// ```
///
/// Bind the save before reading any delegated property. Assigning a property only updates the
/// save in memory. Call `Flixel.save.flush()` when you want to write to disk, for example when the
/// player leaves a settings screen, so assignments never stall the game loop.
package org.flixelgdx.ktx.util;
