package com.example.recappage.model

import com.google.gson.annotations.SerializedName

// Nutrisi
data class NutrisiInfo(
    val energi: String? = "-",
    val protein: String? = "-",
    val lemak: String? = "-",
    val karbo: String? = "-",
    val takaran: String? = "-"
)

// Item Deteksi
// Item Deteksi
data class DetectionItem(
    @SerializedName("nama_makanan")
    val namaMakanan: String,

    @SerializedName("akurasi")
    val akurasi: Double,

    @SerializedName("bbox")
    val boundingBox: List<Double>,

    @SerializedName("detail_nutrisi")
    val nutrisi: NutrisiInfo,

    // 🔥 TAMBAHAN BARU: Field untuk menyimpan URL gambar
    var imageUrl: String = ""
)

// Request ke Gradio
data class DetectionRequest(
    @SerializedName("data")
    val data: List<Map<String, String>>
)