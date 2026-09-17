import com.vanniktech.maven.publish.SonatypeHost
import io.gitlab.arturbosch.detekt.Detekt

/**
 * Convention for FlixelGDX KTX Kotlin library modules.
 *
 * Covers: project coordinates (group/version from root gradle.properties), IDE metadata,
 * Java compile encoding, Kotlin JVM targeting a Java 17 toolchain, the java-library surface area,
 * Spotless formatting (ktfmt Google style), detekt static analysis, Dokka HTML docs, and the
 * Vanniktech Maven publish pipeline targeting Sonatype Central Portal.
 *
 * This single plugin replaces the flixelgdx.java-base + flixelgdx.java-library split used in
 * the base framework; the KTX repo has no Java-only modules, so the baseline is folded in here.
 */

plugins {
  eclipse
  idea
  id("org.jetbrains.kotlin.jvm")
  `java-library`
  id("com.diffplug.spotless")
  id("io.gitlab.arturbosch.detekt")
  id("org.jetbrains.dokka")
  id("com.vanniktech.maven.publish")
}

val groupId: String by project

group = groupId
version = rootProject.version

eclipse.project.name = project.name

idea {
  module {
    outputDir = file("build/classes/kotlin/main")
    testOutputDir = file("build/classes/kotlin/test")
  }
}

tasks.withType<JavaCompile>().configureEach {
  options.encoding = "UTF-8"
}

kotlin {
  jvmToolchain(17)

  // Build against an older Kotlin baseline than the compiler so the published artifact stays
  // consumable by projects on older Kotlin toolchains. The compiler is 2.0.x, but emitting
  // 2.0 metadata and depending on the 2.0 standard library keeps the module readable by
  // any consumer on Kotlin 2.0 or newer.
  coreLibrariesVersion = "2.0.21"

  compilerOptions {
    apiVersion = org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0
    languageVersion = org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0
  }
}

spotless {
  kotlin {
    // ktfmt with Google style gives 2-space indentation, matching the project .editorconfig.
    ktfmt("0.49").googleStyle()
    trimTrailingWhitespace()
    endWithNewline()
  }
  kotlinGradle {
    ktfmt("0.49").googleStyle()
    trimTrailingWhitespace()
    endWithNewline()
  }
}

detekt {
  config.setFrom(rootProject.layout.projectDirectory.file("gradle/detekt/detekt.yml"))
  buildUponDefaultConfig = true
}

tasks.withType<Detekt>().configureEach {
  // detekt fails the build on issues by default (ignoreFailures = false).
  reports {
    html.required = true
    xml.required = false
    txt.required = false
  }
}

// JitPack rewrites Gradle module metadata and drops classifier compatibility data, causing
// consumers to resolve wrong variants. POMs stay correct; omit .module files so metadata is
// sourced from the POM alone. Mirrors flixelgdx.java-library in the base framework.
tasks.matching { it.name.startsWith("generateMetadataFileFor") }.configureEach {
  enabled = false
}

mavenPublishing {
  publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

  val hasSigning = findProperty("flixel.signing.enabled")?.toString() == "true"
    || findProperty("signing.keyId") != null
    || findProperty("signingInMemoryKeyId") != null
  if (hasSigning) {
    signAllPublications()
  }

  coordinates(project.group as String, project.name, project.version as String)

  pom {
    name = rootProject.property("pomName") as String
    description = rootProject.property("pomDescription") as String
    url = rootProject.property("pomUrl") as String
    licenses {
      license {
        name = rootProject.property("pomLicenseName") as String
        url = rootProject.property("pomLicenseUrl") as String
        distribution = "repo"
      }
    }
    developers {
      developer {
        id = rootProject.property("pomDeveloperId") as String
        name = rootProject.property("pomDeveloperName") as String
      }
    }
    scm {
      connection = rootProject.property("pomScmConnection") as String
      developerConnection = rootProject.property("pomScmDeveloperConnection") as String
      url = rootProject.property("pomScmUrl") as String
    }
  }
}
