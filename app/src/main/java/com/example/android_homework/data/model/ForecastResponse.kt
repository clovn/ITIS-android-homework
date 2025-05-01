package com.example.android_homework.data.model

data class ForecastResponse(
    val list: List<ForecastResponseItem>
)

data class ForecastResponseItem(
    val dt: Long,
    val main: Main,
    val weather: List<Weather>
)