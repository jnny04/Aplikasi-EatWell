plugins {
    // Use aliases for consistency
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    // Apply Hilt and KSP plugins
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)

    // Google Services
    id("com.google.gms.google-services")

    // Kapt tetap dibiarkan untuk Hilt (sebelum migrasi full ke KSP)
    kotlin("kapt")
}

android {
    namespace = "com.example.recappage"

    // ✅ Compile SDK Wajib 35 untuk library baru (Credentials, Descope)
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.recappage"
        minSdk = 24

        // ✅ Target SDK Tetap 34 (Biar aman dari perubahan sistem Android 15)
        targetSdk = 34

        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField(
            "String",
            "SPOONACULAR_API_KEY",
            "\"c60ac1be2bf949418c4dba5aff3f97db\""
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "11"
        // Workaround untuk metadata check
        freeCompilerArgs += listOf("-Xskip-metadata-version-check")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    // 🔥🔥 BAGIAN SAKTI PENYELAMAT ERROR GRADLE 🔥🔥
    configurations.all {
        resolutionStrategy.eachDependency {
            // 1. Paksa Kotlin agar aman
            if (requested.group == "org.jetbrains.kotlin" && requested.name.startsWith("kotlin-stdlib")) {
                useVersion("1.9.24")
            }

            // 2. 🔥 PAKSA LIFECYCLE KE VERSI STABIL (2.8.7) 🔥
            // Ini akan memperbaiki error "requires Android Gradle plugin 8.6.0"
            if (requested.group == "androidx.lifecycle" && requested.name.contains("lifecycle")) {
                useVersion("2.8.7")
            }
        }
    }
}

dependencies {
    // Paksa Kotlin BOM agar versi konsisten
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:2.0.21"))

    // --- Dependensi Inti ---
    implementation(libs.androidx.core.ktx)
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    // --- API & Jaringan ---
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("com.google.code.gson:gson:2.10.1")

    // --- Hilt ---
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler) // Hilt masih pakai kapt
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // --- Jetpack Compose ---
    implementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.runtime:runtime-livedata")

    // Preference Datastore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // --- Navigation & Image Loading ---
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("io.coil-kt:coil-compose:2.6.0")

    // --- Room Database (Pakai KSP) ---
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // --- Firebase ---
    implementation(platform("com.google.firebase:firebase-bom:34.5.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    // Pastikan pakai versi viewModel yang aman (sudah dihandle resolutionStrategy di atas)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("com.google.firebase:firebase-storage")

    // --- Security & Biometric ---
    implementation("androidx.biometric:biometric:1.1.0")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // --- CameraX & Permissions ---
    val cameraxVersion = "1.3.1"
    implementation("androidx.camera:camera-core:$cameraxVersion")
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-view:$cameraxVersion")
    implementation("com.google.accompanist:accompanist-permissions:0.34.0")

    // --- DESCOPE (MFA) ---
    implementation("com.descope:descope-kotlin:0.17.5")
    implementation("androidx.browser:browser:1.8.0")

    // --- Testing ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
}