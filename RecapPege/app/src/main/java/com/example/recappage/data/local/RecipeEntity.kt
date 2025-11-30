package com.example.recappage.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.recappage.model.AnalyzedInstruction
import com.example.recappage.model.ExtendedIngredient
import com.example.recappage.model.Recipe

@Entity(tableName = "recipes_table")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true)
    val localId: Int = 0,

    val id: Int,
    val title: String,
    val image: String,
    val summary: String,
    val calories: Double,

    // ✅ KOLOM BARU UNTUK DATA LENGKAP
    val readyInMinutes: Int,
    val servings: Int,
    val aggregateLikes: Int,
    val vegan: Boolean,
    val vegetarian: Boolean,
    val dairyFree: Boolean,
    val glutenFree: Boolean,
    val cheap: Boolean,
    val veryHealthy: Boolean,
    val veryPopular: Boolean,
    val sourceName: String,
    val sourceUrl: String,

    // Data List (disimpan pakai TypeConverter)
    val extendedIngredients: List<ExtendedIngredient>,
    val analyzedInstructions: List<AnalyzedInstruction>
) {

    companion object {
        fun fromRecipe(recipe: Recipe): RecipeEntity {
            // Karena Recipe TIDAK punya nutrition langsung di list, kita set 0.0 atau ambil dari mana saja jika ada logicnya
            val caloriesValue = 0.0

            return RecipeEntity(
                id = recipe.id,
                title = recipe.title,
                image = recipe.image,
                summary = recipe.summary,
                calories = caloriesValue,

                // ✅ SIMPAN DATA LAINNYA
                readyInMinutes = recipe.readyInMinutes,
                servings = recipe.servings,
                aggregateLikes = recipe.aggregateLikes,
                vegan = recipe.vegan,
                vegetarian = recipe.vegetarian,
                dairyFree = recipe.dairyFree,
                glutenFree = recipe.glutenFree,
                cheap = recipe.cheap,
                veryHealthy = recipe.veryHealthy,
                veryPopular = recipe.veryPopular,
                sourceName = recipe.sourceName ?: "", // Handle null
                sourceUrl = recipe.sourceUrl ?: "",   // Handle null
                extendedIngredients = recipe.extendedIngredients,
                analyzedInstructions = recipe.analyzedInstructions
            )
        }
    }

    fun toRecipe(): Recipe {
        return Recipe(
            aggregateLikes = aggregateLikes,
            analyzedInstructions = analyzedInstructions,
            cheap = cheap,
            cookingMinutes = 0, // Tidak disimpan, default
            creditsText = "",
            cuisines = emptyList(),
            dairyFree = dairyFree,
            diets = emptyList(),
            dishTypes = emptyList(),
            extendedIngredients = extendedIngredients, // ✅ Data Bahan dikembalikan
            gaps = "",
            glutenFree = glutenFree,
            healthScore = 0.0,
            id = id,
            image = image,
            imageType = "",
            instructions = "", // Biasanya diambil dari analyzedInstructions
            license = "",
            lowFodmap = false,
            occasions = emptyList(),
            originalId = 0,
            preparationMinutes = 0,
            pricePerServing = 0.0,
            readyInMinutes = readyInMinutes,
            servings = servings,
            sourceName = sourceName,
            sourceUrl = sourceUrl,
            spoonacularScore = 0.0,
            spoonacularSourceUrl = "",
            summary = summary,
            sustainable = false,
            title = title,
            vegan = vegan,
            vegetarian = vegetarian,
            veryHealthy = veryHealthy,
            veryPopular = veryPopular,
            weightWatcherSmartPoints = 0
        )
    }
}