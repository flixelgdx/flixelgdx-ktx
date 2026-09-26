dependencyResolutionManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
  versionCatalogs {
    // Reuse the root build's catalog so plugin versions here stay in sync with the ones
    // subprojects consume, instead of duplicating version numbers by hand.
    create("libs") {
      from(files("../gradle/libs.versions.toml"))
    }
  }
}

rootProject.name = "build-logic"
