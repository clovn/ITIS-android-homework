package com.example.android_homework.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_homework.domain.model.ForecastItem
import com.example.android_homework.domain.model.WeatherDetailed
import com.example.android_homework.domain.usecase.GetCityForecastUseCase
import com.example.android_homework.domain.usecase.GetCityWeatherUseCase
import com.example.android_homework.presentation.mapError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class WeatherInfoState {
    data object Loading: WeatherInfoState()
    data class Loaded(val weatherDetailed: WeatherDetailed): WeatherInfoState()
    data class Error(val message: String): WeatherInfoState()
}

sealed class ForecastInfoState {
    data object Loading: ForecastInfoState()
    data class Loaded(val forecast: List<ForecastItem>): ForecastInfoState()
    data class Error(val message: String): ForecastInfoState()
}

@HiltViewModel
class DetailInfoViewModel @Inject constructor(
    private val getCityForecastUseCase: GetCityForecastUseCase,
    private val getCityWeatherUseCase: GetCityWeatherUseCase
): ViewModel() {

    private val _weatherState = MutableStateFlow<WeatherInfoState>(WeatherInfoState.Loading)
    val weatherState: StateFlow<WeatherInfoState> = _weatherState

    private val _forecastState = MutableStateFlow<ForecastInfoState>(ForecastInfoState.Loading)
    val forecastState: StateFlow<ForecastInfoState> = _forecastState

    private val _toastFlow = MutableSharedFlow<String>(replay = 1)
    val toastFlow: SharedFlow<String> = _toastFlow

    fun fetchWeather(city: String){
        _weatherState.update { WeatherInfoState.Loading }
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                getCityWeatherUseCase.invoke(city)
            }.onSuccess {  weatherWrapper ->
                _weatherState.update {
                    WeatherInfoState.Loaded(weatherWrapper.data)
                }
                _toastFlow.emit(weatherWrapper.message)
            }.onFailure {error ->
                Log.d("DEBUG", error.toString())
                _weatherState.update {
                    WeatherInfoState.Error(mapError(error))
                }
            }
        }
    }

    fun fetchForecast(city: String){
        _forecastState.update { ForecastInfoState.Loading }
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                getCityForecastUseCase.invoke(city)
            }.onSuccess {forecast ->
                _forecastState.update { ForecastInfoState.Loaded(forecast) }
            }.onFailure {error ->
                _forecastState.update { ForecastInfoState.Error(mapError(error)) }
            }
        }
    }
}