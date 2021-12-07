package com.fitboys.nutrimax.data.model

data class Food (
    val name: String? = "",
    val quantity: Int? = 0,
    val calories: Int? = 0,
    val proteins: Int? = 0,
    val fats: Int? = 0,
    val carbohydrates: Int? = 0,
    val recordedDate: String? = "",
    val rating: Double? = 0.0,
    val image: String? = ""
)