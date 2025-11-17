plugins {
    // Use aliases for consistency
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    // Apply Hilt and KSP plugins using their aliases
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)

    // Keep the google-services plugin
    id("com.google.gms.google-services")
    kotlin("kapt")
}

android {
    namespace = "com.example.recappage"
    // Use a currently stable SDK version like 34 if 36 is not out
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.recappage"
        minSdk = 24
        targetSdk = 34 // Match compileSdk
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
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
        buildConfig = true
        // dataBinding can be removed if you are only using Compose
    }
    composeOptions {
        // REMOVE this line. The Compose BOM will manage the compiler version for you.
        // kotlinCompilerExtensionVersion = "1.5.11"
    }
    // REMOVE this block. It's for kapt, which we are no longer using.
    // kapt {
    //     correctErrorTypes = true
    // }
}

dependencies {
    // --- Dependensi Inti ---
    implementation(libs.androidx.core.ktx) // Use version from TOML
    implementation("androidx.appcompat:appcompat:1.7.0") // Align with other libs
    implementation("com.google.android.material:material:1.12.0")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")


    // --- API & Jaringan ---
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("com.google.code.gson:gson:2.10.1")

    // --- Hilt (using ksp) ---
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler) // Use ksp instead of kapt
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
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // --- Navigation & Image Loading ---
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("io.coil-kt:coil-compose:2.6.0") // Use a version compatible with Compose BOM

    // --- Room Database (using ksp) ---
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1") // Use ksp for Room as well

    // --- Firebase ---
    implementation(platform("com.google.firebase:firebase-bom:34.5.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.2")

    // --- Testing ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
}