package com.example.android_homework.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_homework.domain.model.Forecast
import com.example.android_homework.domain.usecase.GetCityForecastUseCase
import com.example.android_homework.presentation.mapError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DetailInfoState {
    data object Idle: DetailInfoState()
    data object Loading: DetailInfoState()
    data class Loaded(val forecast: Forecast): DetailInfoState()
    data class Error(val message: String): DetailInfoState()
}

@HiltViewModel
class DetailInfoViewModel @Inject constructor(
    private val getCityForecastUseCase: GetCityForecastUseCase
): ViewModel() {

    private val _state = MutableStateFlow<DetailInfoState>(DetailInfoState.Idle)
    val state: StateFlow<DetailInfoState> = _state

    fun fetchData(city: String){
        _state.update {
            DetailInfoState.Loading
        }
        viewModelScope.launch {
            runCatching {
                getCityForecastUseCase.invoke(city)
            }.onSuccess {  forecast ->
                _state.update {
                    DetailInfoState.Loaded(forecast)
                }
            }.onFailure {error ->
                Log.d("DEBUG", error.toString())
                _state.update {
                    DetailInfoState.Error(mapError(error))
                }
            }
        }
    }
}