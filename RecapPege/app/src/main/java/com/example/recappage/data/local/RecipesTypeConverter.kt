package com.example.recappage.data.local

import androidx.room.TypeConverter
import com.example.recappage.model.AnalyzedInstruction
import com.example.recappage.model.ExtendedIngredient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RecipesTypeConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromIngredients(ingredients: List<ExtendedIngredient>): String {
        return gson.toJson(ingredients)
    }

    @TypeConverter
    fun toIngredients(data: String): List<ExtendedIngredient> {
        val listType = object : TypeToken<List<ExtendedIngredient>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun fromInstructions(instructions: List<AnalyzedInstruction>): String {
        return gson.toJson(instructions)
    }

    @TypeConverter
    fun toInstructions(data: String): List<AnalyzedInstruction> {
        val listType = object : TypeToken<List<AnalyzedInstruction>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun fromStringList(list: List<String>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toStringList(data: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(data, listType)
    }
}