package com.example.android_homework.presentation

import retrofit2.HttpException
import java.io.IOException

fun mapError(error: Throwable): String = when(error){
    is IOException -> "Проблемы с интернетом"
    is HttpException -> "Не удалось загрузить данные"
    else -> "Неизвестная ошибка"
}
