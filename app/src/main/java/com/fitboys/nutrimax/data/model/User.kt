package com.fitboys.nutrimax.data.model

data class User (
    val username: String,
    val email: String,
    val isNutritionist: Boolean,
    val isAdmin: Boolean,
    val gender: String,
    val weight: Int,
    val height: Int,
    val age: Int,
    val activityLevel: String,
    val target: String,
    val caloriesIntake: Int,
    val remainingCalories: Int,
    val last_activity: String
        )

