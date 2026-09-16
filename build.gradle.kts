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
  id("org.jetbrains.kotlin.jvm") version "2.0.21" apply false
  id("com.diffplug.spotless") version "6.25.0" apply false
  id("com.vanniktech.maven.publish") version "0.28.0" apply false
  id("io.gitlab.arturbosch.detekt") version "1.23.7" apply false
  id("org.jetbrains.dokka") version "1.9.20" apply false
}

val groupId: String by project
val projectVersion: String by project

group = groupId
version = projectVersion

eclipse.project.name = "flixelgdx-ktx"

idea {
  module {
    outputDir = file("build/classes/kotlin/main")
    testOutputDir = file("build/classes/kotlin/test")
  }
}
