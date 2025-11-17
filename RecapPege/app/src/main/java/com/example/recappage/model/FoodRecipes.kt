package com.example.recappage.model

import com.example.recappage.model.Recipe
import com.google.gson.annotations.SerializedName

data class FoodRecipes(

    @SerializedName("results")
    val results: List<Recipe>? = null,

    @SerializedName("recipes")
    val randomRecipes: List<Recipe>? = null,

    val offset: Int? = null,
    val number: Int? = null,
    val totalResults: Int? = null
) {
    val recipes: List<Recipe>
        get() = results ?: randomRecipes ?: emptyList()
}
