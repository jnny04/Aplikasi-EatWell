package com.example.recappage.util

// ✅ Pastikan ini sesuai dengan nama paket aplikasi kamu di AndroidManifest/build.gradle
import com.example.recappage.BuildConfig

object ApiConfig {
    const val BASE_URL = "https://api.spoonacular.com/"
    const val API_KEY = BuildConfig.SPOONACULAR_API_KEY
}