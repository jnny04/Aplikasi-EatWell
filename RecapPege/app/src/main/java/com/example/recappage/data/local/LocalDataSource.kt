package com.example.recappage.data.local

import com.example.recappage.data.local.RecipeEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val recipeDao: RecipeDao
) {
    fun readRecipes(): Flow<List<RecipeEntity>> {
        return recipeDao.readRecipes()
    }

    suspend fun insertRecipes(recipes: List<RecipeEntity>) {
        recipeDao.insertRecipes(recipes)
    }

    suspend fun clearRecipes() {
        recipeDao.clearRecipes()
    }
}
