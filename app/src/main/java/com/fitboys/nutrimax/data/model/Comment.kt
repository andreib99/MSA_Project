package com.fitboys.nutrimax.data.model

data class Comment (
    val commentId: String,
    val userId: String,
    val foodId: String,
    val message: String,
    val date: String,
    val parentCommentId: String
        )