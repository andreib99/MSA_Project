package com.fitboys.nutrimax.data.model

import com.fitboys.nutrimax.helpers.ActivityLevel

data class User (
    val username: String,
    val email: String,
    val password: String,
    val isNutritionist: Boolean,
    val weight: Int,
    val height: Int,
    val age: Int,
    val activityLevel: ActivityLevel,
    val caloriesIntake: Int
        )

