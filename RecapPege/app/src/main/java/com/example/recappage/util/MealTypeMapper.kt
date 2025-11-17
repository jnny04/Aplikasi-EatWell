package com.example.recappage.util

object MealTypeMapper {

    fun toApiType(uiType: String): String? {
        return when (uiType.lowercase()) {
            "all" -> null
            "breakfast" -> "breakfast"
            "heavy meal" -> "main course"
            "snacks" -> "snack"
            "dessert" -> "dessert"
            else -> null
        }
    }
    fun cleanHtml(input: String): String {
        return input
            .replace(Regex("<.*?>"), "")      // buang semua HTML tags
            .replace("&nbsp;", " ")
            .replace("&amp;", "&")
            .trim()
    }

}
