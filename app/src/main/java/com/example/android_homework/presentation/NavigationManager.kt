package com.example.android_homework.presentation

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class NavigationManager {

    private val _navigationEvents = MutableSharedFlow<String>()
    val navigationEvents: SharedFlow<String> = _navigationEvents

    suspend fun navigateTo(route: String) {
        _navigationEvents.emit(route)
    }

    companion object {
        val instance = NavigationManager()
    }
}