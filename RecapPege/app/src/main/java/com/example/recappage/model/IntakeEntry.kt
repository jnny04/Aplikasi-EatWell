package com.example.recappage.model

import java.time.LocalDate

data class IntakeEntry(
    val id: Long = System.currentTimeMillis(),
    val name: String = "",
    val calories: Int = 0,
    val date: LocalDate = LocalDate.now(),
    val imageUrl: String? = null,
    // ✅ TAMBAHKAN 3 KOLOM INI:
    val carbs: Int = 0,
    val protein: Int = 0,
    val fat: Int = 0
)