/**
 * Root settings for the FlixelGDX KTX multi-module build.
 *
 * <p>Declares the build-logic included build so convention plugins are available to all
 * subprojects, centralizes repository declarations, and includes both Kotlin extension modules.
 */

pluginManagement {
  includeBuild("build-logic")
  repositories {
    gradlePluginPortal()
    mavenCentral()
  }
}

dependencyResolutionManagement {
  repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
  repositories {
    mavenCentral()
    mavenLocal()
  }
}

rootProject.name = "flixelgdx-ktx"

include("flixelgdx-ktx-core", "flixelgdx-ktx-async")
