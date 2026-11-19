/**
 * Root settings for the FlixelGDX KTX multi-module build.
 *
 * <p>Declares the build-logic included build so convention plugins are available to all
 * subprojects, centralizes repository declarations, and includes every Kotlin extension module.
 *
 * <p>When sibling checkouts of the framework (../flixelgdx), the UI extension (../flixelgdx-ui), or
 * the video extension (../flixelgdx-video) exist, they are included as composite builds, so the
 * modules compile against local source instead of the published artifacts.
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

include("flixelgdx-ktx-core", "flixelgdx-ktx-ui", "flixelgdx-ktx-video")

if (file("../flixelgdx").isDirectory) {
  includeBuild("../flixelgdx")
}

if (file("../flixelgdx-ui").isDirectory) {
  includeBuild("../flixelgdx-ui")
}

if (file("../flixelgdx-video").isDirectory) {
  includeBuild("../flixelgdx-video")
}
