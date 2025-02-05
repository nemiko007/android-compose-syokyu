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

rootProject.name = "Railway"
include(":app")
include(":auto-semantic-plugin")
project(":auto-semantic-plugin").projectDir = File(".techtrain/auto-semantic-plugin")
