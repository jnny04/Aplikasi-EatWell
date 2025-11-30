package com.example.recappage.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters // ✅ Import ini

@Database(
    entities = [RecipeEntity::class],
    version = 2, // ⚠️ NAIKKAN VERSI DARI 1 KE 2
    exportSchema = false
)
@TypeConverters(RecipesTypeConverter::class) // ✅ DAFTARKAN DI SINI
abstract class RecipeDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao
}