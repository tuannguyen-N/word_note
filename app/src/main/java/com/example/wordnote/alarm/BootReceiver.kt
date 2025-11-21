package com.example.wordnote.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.wordnote.data.AppDatabase
import com.example.wordnote.data.AppPreferences
import com.example.wordnote.data.mapper.toData
import com.example.wordnote.util.NotificationHelper
import com.example.wordnote.util.getDelay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (!AppPreferences.canPostNotifications) return
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return
        if (context == null) return

        val dao = AppDatabase.getInstance(context).wordDao

        CoroutineScope(Dispatchers.IO).launch {

            val words = dao.getAllWordsSync()
            val now = System.currentTimeMillis()

            for (w in words) {
                val word = w.toData()
                if (w.nextTriggerTime <= now) {
                    NotificationHelper.showWordNotification(
                        context,
                        word.word,
                        word.note,
                        word.meanings.first().definitions.first().definition
                    )

                    val next = now + getDelay(w.level)
                    dao.updateLevel(w.id, w.level, next)

                    AlarmScheduler(context).scheduleWord(w.toData(), next)

                } else {
                    AlarmScheduler(context).scheduleWord(w.toData(), w.nextTriggerTime)
                }
            }
        }
    }
}
