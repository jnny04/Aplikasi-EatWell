package com.example.recappage.data

import android.graphics.Bitmap
import android.util.Base64
import com.example.recappage.model.FoodRecipes
import com.example.recappage.model.NutritionWidgetResponse
import com.example.recappage.model.RecipeInformation
import com.example.recappage.util.ApiConfig
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FoodRepository @Inject constructor(
    private val api: FoodRecipesApi,
    private val detectionApi: FoodDetectionApi
) {

    // =========================================================
    // 🔥 AI DETEKSI MAKANAN via GRADIO 4.x (Solusi Final)
    // =========================================================
    suspend fun detectFoodImage(bitmap: Bitmap): Result<String> {
        return try {
            // ============================================
            // TAHAP 1: KIRIM GAMBAR & DAPATKAN TIKET (EVENT_ID)
            // ============================================
            val base64 = bitmapToBase64(bitmap)
            val dataUrl = "data:image/jpeg;base64,$base64"

            val imageData = mapOf(
                "path" to dataUrl,
                "url" to dataUrl,
                "orig_name" to "upload.jpg",
                "size" to base64.length,
                "mime_type" to "image/jpeg",
                "is_stream" to false
            )

            val body = mapOf("data" to listOf(imageData))
            val postResponse = detectionApi.detectFood(body)

            if (!postResponse.isSuccessful) {
                return Result.failure(Exception("Gagal POST: ${postResponse.code()}"))
            }

            // Ambil Event ID dari respon awal
            val postBodyString = postResponse.body()?.string() ?: ""
            val eventId = try {
                org.json.JSONObject(postBodyString).getString("event_id")
            } catch (e: Exception) {
                return Result.failure(Exception("Gagal dapat tiket antrian: $postBodyString"))
            }

            // ============================================
            // TAHAP 2: LOOPING - TANYA HASIL SAMPAI SELESAI
            // ============================================
            var finalResultJson: String? = null
            var attempts = 0

            // Coba cek maksimal 20 kali (20 detik)
            while (attempts < 20) {
                kotlinx.coroutines.delay(1000) // Tunggu 1 detik
                attempts++

                val getResponse = detectionApi.getFoodResult(eventId)
                if (getResponse.isSuccessful) {
                    // Gradio mengirim data stream: "event: complete\ndata: [...]"
                    val streamData = getResponse.body()?.string() ?: ""

                    if (streamData.contains("event: complete")) {
                        // HORE! Selesai. Kita ambil bagian JSON-nya.
                        // Format biasanya:
                        // event: complete
                        // data: [ ...JSON ASLI DISINI... ]

                        val lines = streamData.lines()
                        for (line in lines) {
                            if (line.startsWith("data: ")) {
                                // Hapus tulisan "data: " di depan, sisanya JSON
                                finalResultJson = line.removePrefix("data: ").trim()
                                break
                            }
                        }
                        break // Keluar dari looping
                    }
                    // Jika belum complete (misal "event: generating"), loop lanjut lagi...
                }
            }

            if (finalResultJson != null) {
                Result.success(finalResultJson)
            } else {
                Result.failure(Exception("Waktu habis, server terlalu sibuk."))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Convert bitmap → Base64
    private fun bitmapToBase64(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val bytes = stream.toByteArray()
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }


    // =========================================================
    // 🔵 BAGIAN SPOONACULAR (TIDAK DIUBAH)
    // =========================================================

    suspend fun getRandomRecipes(
        number: Int = 10,
        tags: String? = null,
        excludeTags: String? = null
    ): Response<FoodRecipes> {

        val queries = mutableMapOf(
            "number" to number.toString(),
            "addRecipeInformation" to "true",
            "fillIngredients" to "true",
            "apiKey" to ApiConfig.API_KEY
        )

        tags?.let { queries["tags"] = it }
        excludeTags?.let { queries["excludeIngredients"] = it }

        return api.getRandomRecipes(queries)
    }

    suspend fun getRecipeInformation(id: Int): Response<RecipeInformation> {
        return api.getRecipeInformation(id)
    }

    suspend fun getNutritionDetail(id: Int): Response<NutritionWidgetResponse> {
        return api.getNutritionDetail(id)
    }

    suspend fun searchRecipes(query: String, offset: Int): Response<FoodRecipes> {
        val queries = mutableMapOf(
            "query" to query,
            "number" to "20",
            "offset" to offset.toString(),
            "addRecipeInformation" to "true",
            "apiKey" to ApiConfig.API_KEY
        )
        return api.searchRecipes(queries)
    }

    suspend fun getSearchSuggestions(query: String): Response<FoodRecipes> {
        val queries = mapOf(
            "query" to query,
            "number" to "10",
            "apiKey" to ApiConfig.API_KEY
        )
        return api.searchRecipes(queries)
    }
}