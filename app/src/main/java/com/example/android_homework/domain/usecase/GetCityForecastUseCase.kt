package com.example.android_homework.domain.usecase

import com.example.android_homework.domain.model.Forecast
import com.example.android_homework.domain.model.ForecastItem
import com.example.android_homework.domain.model.WeatherMainInfo
import com.example.android_homework.domain.repository.WeatherRepository
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

class GetCityForecastUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    suspend fun invoke(city: String): Forecast {
        val weather =  weatherRepository.getWeather(city)
        val listForecast = weatherRepository.getForecast(city).map { forecastResponse ->
            ForecastItem(
                temp = forecastResponse.main.temp.toInt(),
                icon = forecastResponse.weather[0].icon,
                date = formatTimestamp(forecastResponse.dt)
            )
        }

        return Forecast(
            weather = WeatherMainInfo(city = weather.name, temp = weather.main.temp.toInt(), main = weather.weather[0].description, icon = weather.weather[0].icon),
            windSpeed = weather.wind.speed,
            feelsLike = weather.main.feelsLike.toInt(),
            humidity = weather.main.humidity,
            pressure = weather.main.pressure,
            list = listForecast
        )
    }

    private fun formatTimestamp(timestamp: Long, zoneId: ZoneId = ZoneId.systemDefault()): String {
        return Instant.ofEpochSecond(timestamp)
            .atZone(zoneId)
            .format(DateTimeFormatter.ofPattern("d MMMM HH:mm", Locale("ru")))
    }
}