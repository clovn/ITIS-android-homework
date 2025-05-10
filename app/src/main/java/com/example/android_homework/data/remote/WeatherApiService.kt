package com.example.android_homework.data.remote

import com.example.android_homework.data.model.ForecastResponse
import com.example.android_homework.data.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("weather")
    suspend fun getWeather(@Query("q") city: String): WeatherResponse

    @GET("forecast")
    suspend fun getForecast(@Query("q") city: String): ForecastResponse
}