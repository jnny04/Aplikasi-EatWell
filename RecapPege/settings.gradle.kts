pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

// 👇 BAGIAN INI YANG PENTING 👇
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        // ✅ JITPACK WAJIB DITARUH DI SINI (AGAR DESCOPE BISA DIDOWNLOAD)
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "RecapPage" // Sesuaikan jika nama project kamu beda
include(":app")