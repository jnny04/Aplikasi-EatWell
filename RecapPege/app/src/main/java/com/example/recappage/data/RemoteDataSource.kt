package com.example.recappage.data

import com.example.recappage.data.FoodRecipesApi
import com.example.recappage.model.FoodRecipes
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val foodRecipesApi: FoodRecipesApi
) {

    suspend fun getRandomRecipes(queries: Map<String, String>): Response<FoodRecipes> {
        return foodRecipesApi.getRandomRecipes(queries)
    }

    suspend fun searchRecipes(queries: Map<String, String>): Response<FoodRecipes> {
        return foodRecipesApi.searchRecipes(queries)
    }

    suspend fun getRecipeInformation(id: Int) =
        foodRecipesApi.getRecipeInformation(id)

    suspend fun getNutritionDetail(id: Int) =
        foodRecipesApi.getNutritionDetail(id)
}