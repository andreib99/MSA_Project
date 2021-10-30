package com.fitboys.nutrimax.data.model

data class Comment (
    val userId: String,
    val foodId: String,
    val message: String,
    val date: String,
    val parentCommentId: String
        )