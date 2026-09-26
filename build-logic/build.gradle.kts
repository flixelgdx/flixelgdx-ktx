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
  implementation(libs.kotlin.gradle.plugin)
  implementation(libs.spotless.gradle.plugin)
  implementation(libs.vanniktech.publish.plugin)
  implementation(libs.detekt.gradle.plugin)
  implementation(libs.dokka.gradle.plugin)
}
