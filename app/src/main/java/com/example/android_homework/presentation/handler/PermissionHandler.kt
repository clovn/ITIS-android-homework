package com.example.android_homework.presentation.handler

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class PermissionsHandler @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun isNotificationPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun requestNotificationPermission(activity: ComponentActivity, onResult: (Boolean) -> Unit) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            onResult(true)
            return
        }

        val launcher = activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            onResult(granted)
        }

        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}