package com.example.android_homework.data.local

import com.example.android_homework.data.model.WeatherResponse
import com.example.android_homework.domain.model.ResultWrapper
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CacheManager @Inject constructor() {

    companion object {
        private const val COOLDOWN_MINUTES = 5
        private const val MAX_RECENT_REQUESTS = 3
    }

    private val cache = HashMap<String, CacheEntry<WeatherResponse>>()
    private val recentRequests = LinkedList<String>()

    suspend fun getOrFetch(key: String, fetchFromApi: suspend () -> WeatherResponse): ResultWrapper<WeatherResponse> {
        if (recentRequests.size >= MAX_RECENT_REQUESTS && !recentRequests.contains(key)) {
            return fetchAndCache(key, fetchFromApi)
        }

        val entry = cache[key]
        if (entry != null && isCacheValid(entry.timestamp)) {
            return ResultWrapper(entry.result, "from cache")
        }
        return fetchAndCache(key, fetchFromApi)
    }

    private fun isCacheValid(timestamp: Long): Boolean {
        val currentTime = System.currentTimeMillis()
        return (currentTime - timestamp) <= COOLDOWN_MINUTES * 60 * 1000L
    }

    private suspend fun fetchAndCache(key: String, fetchFromApi: suspend () -> WeatherResponse): ResultWrapper<WeatherResponse> {
        val result = fetchFromApi()
        cache[key] = CacheEntry(result, System.currentTimeMillis())
        updateRecentRequests(key)
        return ResultWrapper(result, "from api")
    }

    private fun updateRecentRequests(key: String) {
        if (recentRequests.size >= MAX_RECENT_REQUESTS) {
            recentRequests.removeFirst()
        }
        recentRequests.addLast(key)
    }
}