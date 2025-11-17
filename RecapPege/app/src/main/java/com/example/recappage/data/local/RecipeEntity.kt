package com.example.recappage.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.recappage.model.Recipe

@Entity(tableName = "recipes_table")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true)
    val localId: Int = 0,

    val id: Int,
    val title: String,
    val image: String,
    val summary: String,
    val calories: Double
) {

    companion object {
        fun fromRecipe(recipe: Recipe): RecipeEntity {

            // Karena Recipe TIDAK punya nutrition → pakai default 0.0
            val caloriesValue = 0.0

            return RecipeEntity(
                id = recipe.id,
                title = recipe.title,
                image = recipe.image,
                summary = recipe.summary,
                calories = caloriesValue
            )
        }
    }

    /**
     * toRecipe() harus mengikuti struktur model Recipe kamu.
     * Karena Recipe kamu sangat panjang, kita isi default minimal.
     */
    fun toRecipe(): Recipe {
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
            id = id,
            image = image,
            imageType = "",
            instructions = "",
            license = "",
            lowFodmap = false,
            occasions = emptyList(),
            originalId = 0,
            preparationMinutes = 0,
            pricePerServing = 0.0,
            readyInMinutes = 0,
            servings = 0,
            sourceName = "",
            sourceUrl = "",
            spoonacularScore = 0.0,
            spoonacularSourceUrl = "",
            summary = summary,
            sustainable = false,
            title = title,
            vegan = false,
            vegetarian = false,
            veryHealthy = false,
            veryPopular = false,
            weightWatcherSmartPoints = 0
        )
    }
}
