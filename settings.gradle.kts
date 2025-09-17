import org.gradle.api.initialization.resolve.RepositoriesMode
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "catlight"
include(
    ":app",
    ":core:ui",
    ":core:domain",
    ":core:data",
    ":feature:fill"
)
