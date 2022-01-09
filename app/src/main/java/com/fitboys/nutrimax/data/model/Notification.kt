package com.fitboys.nutrimax.data.model

data class Notification(
    val userId: String,
    val message: String,
    val timestamp: String,
    val read: Boolean
)
