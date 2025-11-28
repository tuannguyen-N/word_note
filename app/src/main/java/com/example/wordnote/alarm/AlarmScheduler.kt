package com.example.wordnote.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import com.example.wordnote.data.AppPreferences
import com.example.wordnote.domain.model.WordData

class AlarmScheduler(private val context: Context) {

    fun scheduleWord(word: WordData, triggerAt: Long) {
        if (!AppPreferences.canPostNotifications) return
        val adjustedTrigger = adjustToAllowedTime(triggerAt)

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
            adjustedTrigger,
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

    /*------------- helper functions ---------------*/
    private fun adjustToAllowedTime(triggerAt: Long): Long {
        val startMin = AppPreferences.startTimeNotification
        val endMin = AppPreferences.endTimeNotification
        val timeMin = minutesOfDay(triggerAt)

        return when {
            timeMin < startMin->{
                applyMinutesToday(startMin)
            }

            timeMin > endMin -> {
                val overFlow = timeMin - endMin
                applyMinutesTomorrow(startMin + overFlow)
            }

            else -> triggerAt
        }
    }

    private fun minutesOfDay(millis: Long): Int {
        val cal = Calendar.getInstance().apply { timeInMillis = millis }
        return cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
    }

    private fun applyMinutesToday(minutes: Int): Long {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, minutes / 60)
            set(Calendar.MINUTE, minutes % 60)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    private fun applyMinutesTomorrow(minutes: Int): Long {
        val cal = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, minutes / 60)
            set(Calendar.MINUTE, minutes % 60)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }
}