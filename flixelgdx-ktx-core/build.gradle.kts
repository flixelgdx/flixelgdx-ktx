plugins { id("flixelgdx.kotlin-library") }

dependencies {
  api(libs.flixelgdx.core)
  api(libs.kotlinx.coroutines.core)

  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.junit.jupiter)
  testImplementation(libs.kotlin.test)
  testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.test { useJUnitPlatform() }
