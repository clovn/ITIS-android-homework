package com.example.android_homework.data.repository

import com.example.android_homework.data.model.ForecastResponseItem
import com.example.android_homework.data.model.WeatherResponse
import com.example.android_homework.data.remote.WeatherApiService
import com.example.android_homework.domain.repository.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val weatherApiService: WeatherApiService
) : WeatherRepository {

    override suspend fun getCitiesWeatherList(citiesList: List<String>): List<WeatherResponse> {
        val weatherList = mutableListOf<WeatherResponse>()

        for(city in citiesList){
            weatherList.add(weatherApiService.getWeather(city))
        }

        return weatherList
    }

    override suspend fun getForecast(city: String): List<ForecastResponseItem> = weatherApiService.getForecast(city).list

    override suspend fun getWeather(city: String): WeatherResponse = weatherApiService.getWeather(city)
}