package com.example.android_homework.data.local

data class CacheEntry<T>(
    val result: T,
    val timestamp: Long
)