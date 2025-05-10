package com.example.android_homework.domain.usecase

import com.example.android_homework.domain.model.ResultWrapper
import com.example.android_homework.domain.model.WeatherDetailed
import com.example.android_homework.domain.model.WeatherMainInfo
import com.example.android_homework.domain.repository.WeatherRepository
import javax.inject.Inject

class GetCityWeatherUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    suspend fun invoke(city: String): ResultWrapper<WeatherDetailed> {
        val weatherWrapper =  weatherRepository.getWeather(city)
        val weather = weatherWrapper.data

        return ResultWrapper(
            data = WeatherDetailed(
                weather = WeatherMainInfo(city = weather.name, temp = weather.main.temp.toInt(), main = weather.weather[0].description, icon = weather.weather[0].icon),
                windSpeed = weather.wind.speed,
                feelsLike = weather.main.feelsLike.toInt(),
                humidity = weather.main.humidity,
                pressure = weather.main.pressure,
            ),
            message = weatherWrapper.message
        )
    }
}