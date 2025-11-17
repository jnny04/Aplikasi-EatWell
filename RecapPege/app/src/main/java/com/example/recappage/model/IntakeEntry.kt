package com.example.recappage.model

import java.time.LocalDate

data class IntakeEntry(
    val id: Long,
    val name: String,
    val calories: Int,
    val date: LocalDate = LocalDate.now()
)
