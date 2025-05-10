package com.example.android_homework.domain.usecase

import com.example.android_homework.domain.model.ForecastItem
import com.example.android_homework.domain.repository.WeatherRepository
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

class GetCityForecastUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    suspend fun invoke(city: String): List<ForecastItem> {
        return weatherRepository.getForecast(city).map { forecastResponse ->
            ForecastItem(
                temp = forecastResponse.main.temp.toInt(),
                icon = forecastResponse.weather[0].icon,
                date = formatTimestamp(forecastResponse.dt)
            )
        }

    }

    private fun formatTimestamp(timestamp: Long, zoneId: ZoneId = ZoneId.systemDefault()): String {
        return Instant.ofEpochSecond(timestamp)
            .atZone(zoneId)
            .format(DateTimeFormatter.ofPattern("d MMMM hh:mm", Locale("ru")))
    }
}