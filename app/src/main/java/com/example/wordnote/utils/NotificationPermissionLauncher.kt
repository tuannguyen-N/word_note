package com.example.wordnote.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import com.example.wordnote.data.AppPreferences

sealed interface PermissionResult {
    object Granted : PermissionResult
    object Denied : PermissionResult
    object NeedOpenSettings : PermissionResult
    object ShowRationaleDialog: PermissionResult
}

class NotificationPermissionLauncher(
    private val caller: ActivityResultCaller,
    private val activityProvider: ()-> Activity,
    private val onResult: (PermissionResult)-> Unit
) {
    private val permission = Manifest.permission.POST_NOTIFICATIONS

    private val launcher = caller.registerForActivityResult(ActivityResultContracts.RequestPermission()){isGranted->
        if (isGranted)
            onResult(PermissionResult.Granted)
        else
            onPermissionDenied()
    }

    fun requestPermission(){
        if(AppPreferences.needOpenSettingForNotificationPermission){
            onResult(PermissionResult.NeedOpenSettings)
            return
        }
        launcher.launch(permission)
    }

    private fun onPermissionDenied() {
        if (shouldShowRationale()) {
            onResult(PermissionResult.ShowRationaleDialog)
            return
        }
        if (!shouldShowRationale() && !isPermissionGranted()) {
            AppPreferences.needOpenSettingForReadAudioPermission = true
            onResult(PermissionResult.NeedOpenSettings)
            return
        }
    }

    private fun shouldShowRationale(): Boolean {
        return shouldShowRequestPermissionRationale(activityProvider(), permission)
    }

    fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            activityProvider(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}