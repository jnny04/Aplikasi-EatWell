package com.example.recappage.model


import com.google.gson.annotations.SerializedName

data class Step(
    @SerializedName("ingredients")
    val ingredients: List<Ingredient>,
    @SerializedName("number")
    val number: Int,
    @SerializedName("step")
    val step: String
)