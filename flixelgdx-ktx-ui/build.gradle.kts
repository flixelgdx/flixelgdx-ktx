/**
 * Build file for the flixelgdx-ktx-ui module.
 *
 * <p>Provides Kotlin DSLs and sugar for the FlixelGDX UI extension. Depends on flixelgdx-ktx-core
 * (which transitively brings flixelgdx-core) and flixelgdx-ui.
 */
plugins { id("flixelgdx.kotlin-library") }

dependencies {
  api(project(":flixelgdx-ktx-core"))
  api(libs.flixelgdx.ui)

  testImplementation(libs.flixelgdx.jvm)
  testImplementation(libs.junit.jupiter)
  testImplementation(libs.kotlin.test)
  testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.test { useJUnitPlatform() }
