package com.example.recappage.di

import com.example.recappage.data.FoodDetectionApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object DetectionModule {

    private const val BASE_URL = "https://jenny0412-api-nutrisi-makanan.hf.space/"

    @Singleton
    @Provides
    @Named("DetectionRetrofit")
    fun provideDetectionRetrofit(): Retrofit {

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(90, TimeUnit.SECONDS)
            .readTimeout(90, TimeUnit.SECONDS)
            .writeTimeout(90, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(
                GsonConverterFactory.create()   // ⬅ WAJIB! untuk body Map<String, Any>
            )
            .build()
    }

    @Singleton
    @Provides
    fun provideDetectionApi(
        @Named("DetectionRetrofit") retrofit: Retrofit
    ): FoodDetectionApi {
        return retrofit.create(FoodDetectionApi::class.java)
    }
}