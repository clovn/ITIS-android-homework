package com.example.android_homework.presentation


import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
import android.util.Log
import com.example.android_homework.domain.usecase.SaveDataUseCase
import com.example.android_homework.presentation.handler.NotificationHandler
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AppMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationHandler: NotificationHandler

    @Inject
    lateinit var saveDataUseCase: SaveDataUseCase

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val type = message.data[DATA_TYPE] ?: "-1"
        when (type) {
            "-1" -> {
            }
            "1" -> {
                val titleText = message.data[DATA_TITLE] ?: "title"
                val messageText = message.data[DATA_MESSAGE] ?: "message"
                notificationHandler.showNotification(titleText, messageText)
            }
            "2" -> {
                val data = message.data[DATA_DATA] ?: "unknown"
                CoroutineScope(Dispatchers.IO + Job()).launch {
                    saveDataUseCase.invoke(data)
                }
                Log.d("DEBUG", data)
            }
            "3" -> {
                if (isAppInForeground()){
                    CoroutineScope(Dispatchers.IO).launch {
                        NavigationManager.instance.navigateTo("graph_screen")
                    }
                }
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("DEBUG","TOKEN - $token")
    }

    private fun isAppInForeground(): Boolean {
        val appProcessInfo = ActivityManager.RunningAppProcessInfo()
        ActivityManager.getMyMemoryState(appProcessInfo)
        return appProcessInfo.importance == IMPORTANCE_FOREGROUND
    }

    companion object{
        const val DATA_DATA = "data"
        const val DATA_TITLE = "title"
        const val DATA_MESSAGE = "message"
        const val DATA_TYPE = "type"
    }
}