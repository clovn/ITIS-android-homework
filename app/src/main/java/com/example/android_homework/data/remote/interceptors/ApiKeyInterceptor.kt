package com.example.android_homework.data.remote.interceptors

import okhttp3.Interceptor
import okhttp3.Response

const val API_KEY = "7ba1afca088f0142ae115b972d76a5c5"

class ApiKeyInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val newRequest = request.newBuilder()
            .url(
                request.url.newBuilder()
                    .addQueryParameter("appid", API_KEY)
                    .build()
            )
            .build()

        return chain.proceed(newRequest)
    }
}