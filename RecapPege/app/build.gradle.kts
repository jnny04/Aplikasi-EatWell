plugins {
    // Use aliases for consistency
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    // Apply Hilt and KSP plugins
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp) // ✅ Pastikan plugin ini aktif

    // Google Services
    id("com.google.gms.google-services")

    // Kapt tetap dibiarkan untuk Hilt (jika belum migrasi Hilt ke KSP)
    kotlin("kapt")
}

android {
    namespace = "com.example.recappage"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.example.recappage"
        minSdk = 24
        targetSdk = 35
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
            isMinifyEnabled = false
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

    // 👇 BAGIAN PENTING: KODE CHEAT METADATA 👇
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs += listOf("-Xskip-metadata-version-check")
    }
    // 👆 BATAS KODE CHEAT 👆

    buildFeatures {
        compose = true
        buildConfig = true
    }
    // 👇👇 BAGIAN INI YANG DITAMBAHKAN (Di dalam blok android) 👇👇
    // Ini memaksa library yang 'nakal' untuk menggunakan versi standar library yang aman
    configurations.all {
        resolutionStrategy.eachDependency {
            if (requested.group == "org.jetbrains.kotlin" && requested.name.startsWith("kotlin-stdlib")) {
                useVersion("1.9.24")
            }
        }
    }
    // 👆👆 BATAS AKHIR KODE TAMBAHAN 👆👆
}

dependencies {
    // 👇👇 TAMBAHKAN BARIS INI DI PALING ATAS BLOK DEPENDENCIES 👇👇
    // Ini berfungsi memaksa semua library menggunakan Kotlin 2.0.21 yang benar
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
    kapt(libs.hilt.compiler) // Hilt tetap pakai kapt (biar aman dulu)
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
    implementation(libs.androidx.datastore.preferences)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // --- Navigation & Image Loading ---
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("io.coil-kt:coil-compose:2.6.0")

    // --- Room Database ---
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // 👇 PERBAIKAN UTAMA DISINI: Ganti kapt jadi ksp 👇
    ksp("androidx.room:room-compiler:2.6.1")

    // --- Firebase ---
    implementation(platform("com.google.firebase:firebase-bom:34.5.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.2")
    implementation("com.google.firebase:firebase-storage")

    // --- DESCOPE ---
    // Menggunakan library resmi Maven Central
    implementation("com.descope:descope-kotlin:0.17.5")
    implementation("androidx.browser:browser:1.8.0")

    // --- Testing ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
}