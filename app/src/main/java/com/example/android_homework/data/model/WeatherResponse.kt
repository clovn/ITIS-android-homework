package com.example.android_homework.data.model

data class WeatherResponse(
    val weather: List<Weather>,
    val main: Main,
    val name: String,
    val wind: Wind
)

data class Weather(
    val description: String,
    val icon: String
)

data class Main(
    val temp: Double,
    val feelsLike: Double,
    val humidity: Int,
    val pressure: Int
)

data class Wind(
    val speed: Double
)