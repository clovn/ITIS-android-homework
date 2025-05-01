package com.example.android_homework.domain.model

data class Forecast(
    val weather: WeatherMainInfo,
    val windSpeed: Double,
    val feelsLike: Int,
    val humidity: Int,
    val pressure: Int,
    val list: List<ForecastItem>
)