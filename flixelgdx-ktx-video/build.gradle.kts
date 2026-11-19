/**
 * Build file for the flixelgdx-ktx-video module.
 *
 * <p>Provides coroutine helpers for the FlixelGDX Video extension. Depends on flixelgdx-ktx-core
 * (which brings the coroutine dispatcher and flixelgdx-core) and flixelgdx-video-core.
 */
plugins { id("flixelgdx.kotlin-library") }

dependencies {
  api(project(":flixelgdx-ktx-core"))
  api(libs.flixelgdx.video.core)

  testImplementation(libs.junit.jupiter)
  testImplementation(libs.kotlin.test)
  testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.test { useJUnitPlatform() }
