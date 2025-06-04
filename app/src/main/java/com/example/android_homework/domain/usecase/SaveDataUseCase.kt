package com.example.android_homework.domain.usecase

import com.example.android_homework.data.local.SharedPreferencesManager
import javax.inject.Inject

class SaveDataUseCase @Inject constructor(
    private val sharedPreferencesManager: SharedPreferencesManager
) {

    companion object{
        const val KEY = "key"
    }

    suspend fun invoke(value: String) = sharedPreferencesManager.saveData(KEY, value)
}