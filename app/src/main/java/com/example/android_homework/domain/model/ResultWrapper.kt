package com.example.android_homework.domain.model

data class ResultWrapper<out T>(
    val data: T,
    val message: String
)