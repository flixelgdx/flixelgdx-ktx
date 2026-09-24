/**
 * Build file for the flixelgdx-ktx-ui-async module.
 *
 * <p>Provides coroutine helpers for the FlixelGDX UI extension, such as awaiting a click or showing
 * a dialog that returns a result. Depends on flixelgdx-ktx-ui and flixelgdx-ktx-async.
 */
plugins { id("flixelgdx.kotlin-library") }

dependencies {
  api(project(":flixelgdx-ktx-ui"))
  api(project(":flixelgdx-ktx-async"))

  testImplementation(libs.flixelgdx.jvm)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.junit.jupiter)
  testImplementation(libs.kotlin.test)
  testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.test { useJUnitPlatform() }
