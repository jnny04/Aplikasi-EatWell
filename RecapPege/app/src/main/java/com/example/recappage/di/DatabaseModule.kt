package com.example.recappage.di

import android.content.Context
import androidx.room.Room
import com.example.recappage.data.local.RecipeDao
import com.example.recappage.data.local.RecipeDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): RecipeDatabase {
        return Room.databaseBuilder(
            context,
            RecipeDatabase::class.java,
            "recipes_database"
        )
            .fallbackToDestructiveMigration() // ✅ TAMBAHKAN INI AGAR TIDAK CRASH
            .build()
    }

    @Singleton
    @Provides
    fun provideDao(database: RecipeDatabase): RecipeDao {
        return database.recipeDao()
    }
}