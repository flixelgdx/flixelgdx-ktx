plugins { id("flixelgdx.kotlin-library") }

dependencies {
  api(libs.flixelgdx.core)

  testImplementation(libs.junit.jupiter)
  testImplementation(libs.kotlin.test)
  testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.test { useJUnitPlatform() }
