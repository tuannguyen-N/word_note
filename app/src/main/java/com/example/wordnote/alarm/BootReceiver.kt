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

        val dao = AppDatabase.getInstance(context).wordDao
        val alarmScheduler = AlarmScheduler(context)
        val now = System.currentTimeMillis()

        CoroutineScope(Dispatchers.IO).launch {
            val list = dao.getWordByStudiedTime()

            for (entity in list) {
                val data = entity.toData()

                if (entity.nextTriggerTime <= now) {
                    withContext(Dispatchers.Main) {
                        NotificationHelper.showWordNotification(context, data)
                    }

                    val level = WordLevel.fromScore(entity.score)
                    val next = now + level.getDelay()

                    dao.updateLevel(entity.id, level.ordinal + 1, next)
                    alarmScheduler.scheduleWord(data, next)
                } else {
                    alarmScheduler.scheduleWord(data, entity.nextTriggerTime)
                }
            }
        }
    }
}

