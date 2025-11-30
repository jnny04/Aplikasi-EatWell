package com.example.recappage.di

import android.content.Context
import android.util.Log // ✅ Jangan lupa import ini
import com.example.recappage.util.ApiConfig
import com.example.recappage.data.FoodRecipesApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    @Singleton
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        // 1. Buat File Cache (Ukuran 5 MB)
        val cacheSize = 5 * 1024 * 1024L // 5 MB
        val cache = Cache(File(context.cacheDir, "http_cache"), cacheSize)

        // Logging Bawaan (Untuk melihat Body JSON)
        val logging = HttpLoggingInterceptor().apply {
            if (com.example.recappage.BuildConfig.DEBUG) {
                level = HttpLoggingInterceptor.Level.BODY
            } else {
                level = HttpLoggingInterceptor.Level.NONE
            }
        }

        return OkHttpClient.Builder()
            .cache(cache) // ✅ Pasang Cache
            .addInterceptor(logging) // Logging standar
            .addInterceptor { chain ->
                // --- BAGIAN INI DIMODIFIKASI UNTUK PEMBUKTIAN ---

                // 1. Manipulasi Request: Paksa Cache valid selama 60 detik
                var request = chain.request()
                request = request.newBuilder()
                    .header("Cache-Control", "public, max-age=" + 60)
                    .build()

                // 2. Eksekusi Request
                val response = chain.proceed(request)

                // 3. 🔥 CEK SUMBER DATA (LOGGING BUKTI)
                if (response.cacheResponse != null) {
                    // Jika cacheResponse ada, berarti data diambil dari DISK (Hemat Baterai)
                    Log.d("CACHE_TEST", "⚡ HEMAT BATERAI: Data diambil dari CACHE (Radio Idle). URL: ${request.url}")
                } else if (response.networkResponse != null) {
                    // Jika networkResponse ada, berarti data diambil dari SERVER (Boros Baterai)
                    Log.d("CACHE_TEST", "🌐 BOROS BATERAI: Data diambil dari INTERNET (Radio Active). URL: ${request.url}")
                }

                // Kembalikan response agar aplikasi jalan
                response
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        gson: Gson,
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideFoodRecipesApi(retrofit: Retrofit): FoodRecipesApi {
        return retrofit.create(FoodRecipesApi::class.java)
    }
}
