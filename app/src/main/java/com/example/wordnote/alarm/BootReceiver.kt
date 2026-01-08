package com.example.wordnote.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.wordnote.data.AppDatabase
import com.example.wordnote.data.AppPreferences
import com.example.wordnote.data.mapper.toData
import com.example.wordnote.utils.NotificationHelper
import com.example.wordnote.utils.WordLevel
import com.example.wordnote.utils.getDelay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return
        if (!AppPreferences.canPostNotifications) return

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dao = AppDatabase.getInstance(context).wordDao
                val scheduler = AlarmScheduler(context)
                val now = System.currentTimeMillis()

                dao.getWordByStudiedTime().forEach { entity ->
                    val trigger = entity.nextTriggerTime

                    val nextTime = if (trigger > now) {
                        trigger
                    } else {
                        //reschedule
                        now + WordLevel.fromScore(entity.score).getDelay()
                    }

                    scheduler.scheduleWord(
                        entity.toData(),
                        nextTime
                    )
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}

