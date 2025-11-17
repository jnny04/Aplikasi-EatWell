pluginManagement {
    repositories {
        google()
        mavenCentral() // Tambahkan ini
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

rootProject.name = "RecapPage"
include(":app")
