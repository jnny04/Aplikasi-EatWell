package com.example.recappage.data.intake

data class IntakeState(
    val totalCalories: Int = 0,
    val items: List<IntakeItem> = emptyList()
)

data class IntakeItem(
    val name: String,
    val calories: Int
)
