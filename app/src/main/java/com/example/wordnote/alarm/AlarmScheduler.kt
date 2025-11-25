package com.example.wordnote.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.wordnote.domain.model.WordData

class AlarmScheduler(private val context: Context) {

    fun scheduleWord(word: WordData, triggerAt: Long) {
        val intent = Intent(context, WordReceiver::class.java).apply {
            putExtra("ID", word.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            word.id!!,
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

    fun stopScheduleWord(wordId: Int) {
        val intent = Intent(context, WordReceiver::class.java).apply {
            putExtra("ID", wordId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            wordId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarm.cancel(pendingIntent)
    }
}