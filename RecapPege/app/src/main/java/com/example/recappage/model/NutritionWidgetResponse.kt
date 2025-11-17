package com.example.recappage.model

import com.google.gson.annotations.SerializedName

data class NutritionWidgetResponse(
    @SerializedName("nutrients")
    val nutrients: List<NutrientItem>,

    @SerializedName("caloricBreakdown")
    val caloricBreakdown: CaloricBreakdown,

    @SerializedName("weightPerServing")
    val weightPerServing: WeightPerServing
)

data class NutrientItem(
    @SerializedName("name") val name: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("unit") val unit: String,
    @SerializedName("percentOfDailyNeeds") val percentOfDailyNeeds: Double
)

data class CaloricBreakdown(
    @SerializedName("percentProtein") val percentProtein: Double,
    @SerializedName("percentFat") val percentFat: Double,
    @SerializedName("percentCarbs") val percentCarbs: Double
)

data class WeightPerServing(
    @SerializedName("amount") val amount: Int,
    @SerializedName("unit") val unit: String
)
