package com.example.android_homework.data.local

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class SharedPreferencesManager @Inject constructor(@ApplicationContext private val context: Context) {

    companion object {
        const val PREFS_NAME = "AppPrefs"
    }

    private val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    suspend fun saveData(key: String, value: String) {
        withContext(Dispatchers.IO){
            sharedPreferences.edit {
                putString(key, value)
                apply()
            }
        }
    }
}