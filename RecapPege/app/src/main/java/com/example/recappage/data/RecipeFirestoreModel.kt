package com.example.recappage.data

data class RecipeFirestoreModel(
    val id: Int = 0,
    val title: String = "",
    val image: String = "",
    val summary: String = "",
    val calories: Double = 0.0
)
