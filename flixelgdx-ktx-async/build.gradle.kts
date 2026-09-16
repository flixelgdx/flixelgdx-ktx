/**
 * Build file for the flixelgdx-ktx-async module.
 *
 * <p>Provides coroutine-based async extensions for FlixelGDX. Depends on flixelgdx-ktx-core (which
 * transitively brings flixelgdx-core) and kotlinx-coroutines-core.
 */
plugins { id("flixelgdx.kotlin-library") }

dependencies {
  api(project(":flixelgdx-ktx-core"))
  api(libs.kotlinx.coroutines.core)

  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.junit.jupiter)
  testImplementation(libs.kotlin.test)
  testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.test { useJUnitPlatform() }
