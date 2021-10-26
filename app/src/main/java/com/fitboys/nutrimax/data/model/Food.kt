package com.fitboys.nutrimax.data.model

data class Food (
    val foodId: String,
    val userId: String,
    val name: String,
    val quantity: Int,
    val calories: Int,
    val proteins: Int,
    val fats: Int,
    val carbohydrates: Int,
    val recordedDate: String,
    val rating: Double,
    val Image: String
        )