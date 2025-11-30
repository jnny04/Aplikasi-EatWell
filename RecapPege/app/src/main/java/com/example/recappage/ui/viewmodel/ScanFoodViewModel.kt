package com.example.recappage.ui.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recappage.data.FoodRepository
import com.example.recappage.data.IntakeRepository
import com.example.recappage.model.DetectionItem
import com.example.recappage.model.IntakeEntry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ScanFoodViewModel @Inject constructor(
    private val repository: FoodRepository,
    private val intakeRepository: IntakeRepository
) : ViewModel() {

    // URL Server (Pastikan persis seperti ini, tanpa slash di akhir)
    private val BASE_URL = "https://jenny0412-api-nutrisi-makanan.hf.space"

    // UI State
    var isLoading = mutableStateOf(false)
    var detectionResult = mutableStateOf<DetectionItem?>(null)
    var errorMessage = mutableStateOf<String?>(null)

    // ============================================================
    // 🔥 1. SCAN GAMBAR
    // ============================================================
    fun scanImage(bitmap: Bitmap) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            detectionResult.value = null

            val result = repository.detectFoodImage(bitmap)

            result.onSuccess { rawJson ->
                try {
                    // Log ini PENTING untuk melihat apa yang dikirim server
                    Log.d("ScanVM", "RAW SERVER RESPONSE: $rawJson")

                    val detectedItem = parseGradioJson(rawJson)

                    if (detectedItem != null) {
                        detectionResult.value = detectedItem
                    } else {
                        errorMessage.value = "Makanan tidak dikenali."
                    }

                } catch (e: Exception) {
                    errorMessage.value = "Gagal membaca hasil: ${e.message}"
                    Log.e("ScanViewModel", "Parsing error", e)
                }
            }

            result.onFailure {
                errorMessage.value = "Gagal koneksi: ${it.message}"
                Log.e("ScanViewModel", "API error", it)
            }

            isLoading.value = false
        }
    }

    // ============================================================
    // 🔥 2. PARSE JSON (LOGIKA PINTAR UNTUK GAMBAR DETEKSI)
    // ============================================================
    private fun parseGradioJson(raw: String): DetectionItem? {
        try {
            // Gradio 4.x mengembalikan Array: [ImageResult, NutritionResult]
            // ImageResult (Index 0) adalah gambar dengan kotak hijau!
            val jsonArray = JSONArray(raw)
            if (jsonArray.length() < 2) return null

            // --- TAHAP 1: AMBIL PATH GAMBAR (INDEX 0) ---
            val imageElement = jsonArray.get(0)
            var rawPath = ""

            // Server bisa mengirim dalam format Object {"path":...} atau String langsung
            if (imageElement is JSONObject) {
                // Cek key 'url' dulu, kalau tidak ada baru cek 'path'
                rawPath = imageElement.optString("url", imageElement.optString("path"))
            } else {
                rawPath = imageElement.toString()
            }

            // --- TAHAP 2: RAKIT URL FINAL ---
            // Kita harus memastikan URL diawali dengan domain server
            val finalImageUrl = if (rawPath.startsWith("http")) {
                // Jika server sudah kasih link lengkap (http...), pakai langsung
                rawPath
            } else {
                // Jika server cuma kasih buntutnya (misal: /file=/tmp/...), kita tempel ke BASE_URL
                // Format Gradio biasanya butuh "/file=" di tengah
                if (rawPath.startsWith("/file=")) {
                    "$BASE_URL$rawPath"
                } else {
                    "$BASE_URL/file=$rawPath"
                }
            }

            Log.d("ScanVM", "✅ LINK GAMBAR FINAL: $finalImageUrl")

            // --- TAHAP 3: AMBIL DATA NUTRISI (INDEX 1) ---
            val rawNutritionData = jsonArray.get(1).toString()
            val type = object : TypeToken<List<DetectionItem>>() {}.type
            val items: List<DetectionItem> = Gson().fromJson(rawNutritionData, type)

            if (items.isEmpty()) return null

            // Ambil item dengan akurasi tertinggi
            val bestItem = items.maxByOrNull { it.akurasi }

            // Masukkan Link Gambar Hasil Deteksi ke dalam item
            bestItem?.imageUrl = finalImageUrl

            return bestItem

        } catch (e: Exception) {
            Log.e("ScanVM", "JSON Parse error: $raw", e)
            return null
        }
    }

    // ============================================================
    // 🔥 3. SIMPAN KE DIARY
    // ============================================================
    fun saveResultToDiary(item: DetectionItem) {
        viewModelScope.launch {
            val entry = IntakeEntry(
                id = System.currentTimeMillis(),
                name = item.namaMakanan.replace("_", " ").uppercase(),
                calories = parseNum(item.nutrisi.energi),
                carbs = parseNum(item.nutrisi.karbo),
                protein = parseNum(item.nutrisi.protein),
                fat = parseNum(item.nutrisi.lemak),
                date = LocalDate.now()
            )

            intakeRepository.addIntake(entry)
            resetState()
        }
    }

    private fun parseNum(text: String?): Int {
        if (text == null || text == "-") return 0
        val filtered = text.replace(Regex("[^0-9.]"), "")
        return filtered.toDoubleOrNull()?.toInt() ?: 0
    }

    fun resetState() {
        detectionResult.value = null
        errorMessage.value = null
    }
}