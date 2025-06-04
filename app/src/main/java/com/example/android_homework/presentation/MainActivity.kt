package com.example.android_homework.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import android.provider.Settings
import android.widget.Toast
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.android_homework.presentation.handler.PermissionsHandler
import com.example.android_homework.presentation.theme.AndroidhomeworkTheme
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.crashlytics.setCustomKeys
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var permissionsHandler: PermissionsHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            AndroidhomeworkTheme {
                Surface(
                    modifier = Modifier
                        .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
                        .fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WeatherAppNavHost()
                }
            }
        }

        Firebase.crashlytics.setCustomKeys {
            key(DEVICE_ID, Settings.Secure.ANDROID_ID)
        }

        if(!permissionsHandler.isNotificationPermissionGranted()){
            permissionsHandler.requestNotificationPermission(this) {isGranted ->
                if (isGranted) {
                    Toast.makeText(this, "Разрешено", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Запрещено", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    companion object{
        const val DEVICE_ID = "device_id"
    }
}
