package com.example.android_homework.domain.model

data class WeatherDetailed(
    val weather: WeatherMainInfo,
    val windSpeed: Double,
    val feelsLike: Int,
    val humidity: Int,
    val pressure: Int,
)