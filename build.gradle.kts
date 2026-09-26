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
  id("org.jetbrains.kotlin.jvm") version "2.2.0" apply false
  id("com.diffplug.spotless") version "8.10.2" apply false
  id("com.vanniktech.maven.publish") version "0.29.0" apply false
  id("io.gitlab.arturbosch.detekt") version "1.23.7" apply false
  id("org.jetbrains.dokka") version "1.9.20" apply false
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
