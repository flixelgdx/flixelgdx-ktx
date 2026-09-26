/**
 * Root aggregator for the FlixelGDX KTX multi-module build.
 *
 * <p>The root project holds no source itself. All subproject setup lives in the convention
 * plugins under build-logic/src/main/kotlin/. Plugins are declared here in the root classpath
 * scope to prevent classloader conflicts when convention plugins apply them to subprojects.
 */

plugins {
  eclipse
  idea
  alias(libs.plugins.kotlin.jvm) apply false
  alias(libs.plugins.spotless) apply false
  alias(libs.plugins.vanniktech) apply false
  alias(libs.plugins.detekt) apply false
  alias(libs.plugins.dokka) apply false
}

val groupId: String by project

group = groupId
version = gitVersion()

eclipse.project.name = "flixelgdx-ktx"

fun gitVersion(): String = try {
  val proc = ProcessBuilder("git", "describe", "--tags", "--abbrev=0")
    .directory(rootDir)
    .start()
  proc.waitFor()
  proc.inputStream.bufferedReader().readText().trim().removePrefix("v").ifEmpty { "unspecified" }
} catch (_: Exception) {
  "unspecified"
}

idea {
  module {
    outputDir = file("build/classes/kotlin/main")
    testOutputDir = file("build/classes/kotlin/test")
  }
}
