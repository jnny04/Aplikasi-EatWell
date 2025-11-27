package com.example.recappage.di

import android.content.Context
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
        // Cache akan disimpan di folder cache aplikasi di HP
        val cacheSize = 5 * 1024 * 1024L // 5 MB
        val cache = Cache(File(context.cacheDir, "http_cache"), cacheSize)

        // Logging untuk melihat request/response di Logcat (Debug)
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .cache(cache) // ✅ Pasang Cache di sini
            .addInterceptor(logging)
            // ✅ Interceptor untuk mengatur aturan Cache
            .addInterceptor { chain ->
                var request = chain.request()

                // Aturan: Jika ada internet, data dianggap valid selama 60 detik.
                // Jika user buka resep yang sama dalam 60 detik, tidak akan download ulang.
                request = request.newBuilder()
                    .header("Cache-Control", "public, max-age=" + 60)
                    .build()

                chain.proceed(request)
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