package com.example.recappage.data

import com.example.recappage.model.FoodRecipes
import com.example.recappage.model.NutritionWidgetResponse
import com.example.recappage.model.RecipeInformation
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap
import com.example.recappage.BuildConfig


interface FoodRecipesApi {

    @GET("recipes/random")
    suspend fun getRandomRecipes(
        @QueryMap queries: Map<String, String>
    ): Response<FoodRecipes>
    

    @GET("recipes/{id}/information")
    suspend fun getRecipeInformation(
        @Path("id") id: Int,
        @Query("includeNutrition") includeNutrition: Boolean = true,
        @Query("apiKey") apiKey: String = BuildConfig.SPOONACULAR_API_KEY
    ): Response<RecipeInformation>

    @GET("recipes/{id}/nutritionWidget.json")
    suspend fun getNutritionDetail(
        @Path("id") id: Int,
        @Query("apiKey") apiKey: String = BuildConfig.SPOONACULAR_API_KEY
    ): Response<NutritionWidgetResponse>

    @GET("recipes/complexSearch")
    suspend fun searchRecipes(
        @QueryMap queries: Map<String, String>
    ): Response<FoodRecipes>
}
