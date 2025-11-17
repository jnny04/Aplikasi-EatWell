package com.example.recappage.data

import android.provider.SyncStateContract
import com.example.recappage.data.FoodRecipesApi
import com.example.recappage.model.FoodRecipes
import com.example.recappage.model.NutritionWidgetResponse
import com.example.recappage.model.RecipeInformation
import com.example.recappage.util.ApiConfig
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FoodRepository @Inject constructor(
    private val api: FoodRecipesApi
) {

    suspend fun getRandomRecipes(
        number: Int = 10,
        tags: String? = null,
        excludeTags: String? = null
    ): Response<FoodRecipes> {

        val queries = mutableMapOf(
            "number" to number.toString(),
            "addRecipeInformation" to "true",
            "fillIngredients" to "true",
            "apiKey" to ApiConfig.API_KEY            // WAJIB
        )
        tags?.let { queries["tags"] = it }
        excludeTags?.let { queries["excludeIngredients"] = it
        }
        return api.getRandomRecipes(queries)
    }

    suspend fun getRecipeInformation(id: Int): Response<RecipeInformation> {
        return api.getRecipeInformation(id)
    }

    suspend fun getNutritionDetail(id: Int): Response<NutritionWidgetResponse> {
        return api.getNutritionDetail(id)
    }

    suspend fun searchRecipes(query: String): Response<FoodRecipes> {

        val queries = mutableMapOf(
            "query" to query,
            "number" to "20",
            "addRecipeInformation" to "true",
            "apiKey" to ApiConfig.API_KEY   // <-- ini yang perlu ditambah
        )

        return api.searchRecipes(queries)
    }

    suspend fun getSearchSuggestions(query: String): Response<FoodRecipes> {
        val queries = mapOf(
            "query" to query,
            "number" to "10",
            "apiKey" to ApiConfig.API_KEY
        )
        return api.searchRecipes(queries)
    }

}
