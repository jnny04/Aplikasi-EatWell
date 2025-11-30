package com.example.recappage.data

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FoodDetectionApi {

    // 1. Endpoint Minta Tiket (POST)
    @POST("gradio_api/call/deteksi_dan_nutrisi")
    suspend fun detectFood(
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Response<ResponseBody>

    // 2. Endpoint Cek Hasil (GET)
    // Kita kirim event_id di ujung URL
    @GET("gradio_api/call/deteksi_dan_nutrisi/{eventId}")
    suspend fun getFoodResult(
        @Path("eventId") eventId: String
    ): Response<ResponseBody>
}