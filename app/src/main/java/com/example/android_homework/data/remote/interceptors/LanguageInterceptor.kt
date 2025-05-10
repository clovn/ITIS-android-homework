package com.example.android_homework.data.remote.interceptors

import okhttp3.Interceptor
import okhttp3.Response

class LanguageInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val newRequest = request.newBuilder()
            .url(
                request.url.newBuilder()
                    .addQueryParameter("lang", "ru")
                    .build()
            )
            .build()

        return chain.proceed(newRequest)
    }
}