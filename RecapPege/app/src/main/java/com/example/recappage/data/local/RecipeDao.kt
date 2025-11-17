package com.example.recappage.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.recappage.data.local.RecipeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipes: List<RecipeEntity>)

    @Query("SELECT * FROM recipes_table ORDER BY id DESC")
    fun readRecipes(): Flow<List<RecipeEntity>>

    @Query("DELETE FROM recipes_table")
    suspend fun clearRecipes()
}
