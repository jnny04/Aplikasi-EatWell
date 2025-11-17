package com.example.recappage.util

import com.example.recappage.ui.viewmodel.FilterState

object FilterMapper {

    fun toQueries(filter: FilterState): Map<String, String> {

        val includeTags = mutableListOf<String>()
        val excludeTags = mutableListOf<String>()

        // ===============================
        // 🌱 DIETARY PREFERENCES
        // ===============================

        if (filter.vegan) includeTags += "vegan"
        if (filter.vegetarian) includeTags += "vegetarian"

        // low carb = ketogenic diet
        if (filter.lowCarb) includeTags += "ketogenic"

        if (filter.pescatarian) includeTags += "pescetarian"

        // intolerances (exclude ingredients)
        if (filter.glutenFree) includeTags += "gluten free"
        if (filter.dairyFree) includeTags += "dairy free"
        if (filter.eggFree) excludeTags += "egg"
        if (filter.nutsFree) excludeTags += "nuts"

        // seafood free
        if (filter.noSeafood) excludeTags += "seafood"

        // halal = exclude pork family
        if (filter.halal) {
            excludeTags += listOf(
                "pork", "bacon", "ham", "sausage", "lard"
            )
        }

        // ===============================
        // 🌍 CUISINE MAPPING
        // ===============================

        if (filter.asian) includeTags += "asian"
        if (filter.chinese) includeTags += "chinese"
        if (filter.japanese) includeTags += "japanese"
        if (filter.korean) includeTags += "korean"
        if (filter.indian) includeTags += "indian"
        if (filter.italian) includeTags += "italian"
        if (filter.european) includeTags += "european"
        if (filter.thai) includeTags += "thai"

        // ===============================
        // 🍽 DEFAULT MEAL TYPE
        // Selalu tambahkan kalau user tidak pilih kategori
        // supaya hasil tidak kosong
        // ===============================
        if (includeTags.isEmpty()) {
            includeTags += "main course"
        }

        // ===============================
        // 🔍 BUILD FINAL QUERY
        // ===============================

        val queries = mutableMapOf<String, String>()

        queries["number"] = filter.number.toString()
        queries["addRecipeInformation"] = "true"
        queries["fillIngredients"] = "true"
        queries["apiKey"] = ApiConfig.API_KEY

        if (includeTags.isNotEmpty()) {
            queries["tags"] = includeTags.joinToString(",")
        }

        if (excludeTags.isNotEmpty()) {
            queries["exclude-tags"] = excludeTags.joinToString(",")
        }

        if (filter.query.isNotEmpty()) {
            queries["query"] = filter.query
        }

        return queries
    }
}
