/**
 * Build logic for FlixelGDX KTX convention plugins.
 *
 * <p>Every plugin declared in src/main/kotlin/ is compiled against the dependencies listed here,
 * so their types and extensions are available to the precompiled script plugins without needing
 * a buildscript block in each applying project.
 */
plugins {
  `kotlin-dsl`
}

repositories {
  mavenCentral()
  gradlePluginPortal()
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.2.0")
  implementation("com.diffplug.spotless:spotless-plugin-gradle:6.25.0")
  implementation("com.vanniktech:gradle-maven-publish-plugin:0.29.0")
  implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.23.8")
  implementation("org.jetbrains.dokka:dokka-gradle-plugin:1.9.20")
}
