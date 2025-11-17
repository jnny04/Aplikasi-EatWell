package com.example.recappage.ui.viewmodel

data class FilterState(
    // Dietary
    val vegetarian: Boolean = false,
    val vegan: Boolean = false,
    val halal: Boolean = false,           // (tidak native di Spoonacular, akan dipetakan ke includeIngredients / exclude)
    val lowCarb: Boolean = false,
    val pescatarian: Boolean = false,
    val nutsFree: Boolean = false,
    val dairyFree: Boolean = false,
    val glutenFree: Boolean = false,
    val eggFree: Boolean = false,
    val noSeafood: Boolean = false,

    // Cuisine
    val asian: Boolean = false,
    val european: Boolean = false,
    val thai: Boolean = false,
    val chinese: Boolean = false,
    val korean: Boolean = false,
    val japanese: Boolean = false,
    val italian: Boolean = false,
    val indian: Boolean = false,

    // General
    val query: String = "",
    val number: Int = 10
)
