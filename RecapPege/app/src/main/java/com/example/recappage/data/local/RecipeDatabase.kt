package com.example.recappage.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.recappage.data.local.RecipeEntity

@Database(
    entities = [RecipeEntity::class],
    version = 1,
    exportSchema = false
)
abstract class RecipeDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao
}
