package com.example.android_homework.domain.usecase

import com.example.android_homework.data.Data
import com.example.android_homework.domain.model.WeatherMainInfo
import com.example.android_homework.domain.repository.WeatherRepository
import javax.inject.Inject

class GetCitiesWeatherListUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {

    suspend fun invoke(): List<WeatherMainInfo> {
        return weatherRepository.getCitiesWeatherList(Data.citiesList).map { weatherResponse ->
            WeatherMainInfo(
                city = weatherResponse.name,
                temp = weatherResponse.main.temp.toInt(),
                main = weatherResponse.weather[0].description,
                icon = weatherResponse.weather[0].icon
            )
        }
    }
}