package com.example.wordnote.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

class AlarmScheduler(private val context: Context) {

    fun scheduleWord(word: String, note: String?, definition: String, level: Int, triggerAt: Long) {

        val intent = Intent(context, WordReceiver::class.java).apply {
            putExtra("WORD", word)
            putExtra("NOTE", note)
            putExtra("DEFINITION", definition)
            putExtra("LEVEL", level)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            word.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarm.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAt,
            pendingIntent
        )
    }
}