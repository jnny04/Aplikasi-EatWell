package com.example.recappage.data

import com.example.recappage.model.*
import com.google.gson.annotations.SerializedName

// =======================================================
// 1. RECIPE → FIRESTORE MODEL
// =======================================================
fun Recipe.toFirestoreModel(): RecipeFirestoreModel {

    // Karena API kamu tidak menyediakan nutrition,
    // kita set 0.0 supaya tidak error.
    val caloriesValue = 0.0

    return RecipeFirestoreModel(
        id = this.id,
        title = this.title,
        image = this.image,
        summary = this.summary,
        calories = caloriesValue
    )
}

// =======================================================
// 2. FIRESTORE → RECIPE (untuk ditampilkan kembali di UI)
// =======================================================
fun RecipeFirestoreModel.toRecipe(): Recipe {
    return Recipe(
        aggregateLikes = 0,
        analyzedInstructions = emptyList(),
        cheap = false,
        cookingMinutes = 0,
        creditsText = "",
        cuisines = emptyList(),
        dairyFree = false,
        diets = emptyList(),
        dishTypes = emptyList(),
        extendedIngredients = emptyList(),
        gaps = "",
        glutenFree = false,
        healthScore = 0.0,
        id = this.id,
        image = this.image,
        imageType = "",
        instructions = "",
        license = "",
        lowFodmap = false,
        occasions = emptyList(),
        originalId = 0,  // <--- INILAH YANG BIKIN ERROR KALAU TIDAK ADA
        preparationMinutes = 0,
        pricePerServing = 0.0,
        readyInMinutes = 0,
        servings = 0,
        sourceName = "",
        sourceUrl = "",
        spoonacularScore = 0.0,
        spoonacularSourceUrl = "",
        summary = this.summary,
        sustainable = false,
        title = this.title,
        vegan = false,
        vegetarian = false,
        veryHealthy = false,
        veryPopular = false,
        weightWatcherSmartPoints = 0
    )
}
