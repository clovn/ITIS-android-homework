package com.example.android_homework.domain.repository

import com.example.android_homework.data.model.ForecastResponseItem
import com.example.android_homework.data.model.WeatherResponse
import com.example.android_homework.domain.model.ResultWrapper

interface WeatherRepository {
    suspend fun getCitiesWeatherList(citiesList: List<String>): List<WeatherResponse>

    suspend fun getForecast(city: String): List<ForecastResponseItem>

    suspend fun getWeather(city: String): ResultWrapper<WeatherResponse>
}