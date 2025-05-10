package com.example.android_homework.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_homework.domain.model.WeatherMainInfo
import com.example.android_homework.domain.usecase.GetCitiesWeatherListUseCase
import com.example.android_homework.presentation.mapError
import kotlinx.coroutines.flow.MutableStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.net.ssl.HttpsURLConnection

sealed class UiState {
    data object Idle: UiState()
    data object Loading: UiState()
    data class Loaded(val weatherList: List<WeatherMainInfo>): UiState()
    data class Error(val message: String): UiState()
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getCitiesWeatherListUseCase: GetCitiesWeatherListUseCase
): ViewModel() {

    private val _uistate = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uistate

    fun fetchData(){
        _uistate.update {
            UiState.Loading
        }
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                getCitiesWeatherListUseCase.invoke()
            }.onSuccess { weatherList ->
                _uistate.update {
                    UiState.Loaded(weatherList)
                }
            }.onFailure { error ->
                Log.d("ERROR", error.toString())
                _uistate.update { UiState.Error(mapError(error)) }
            }
        }
    }
}