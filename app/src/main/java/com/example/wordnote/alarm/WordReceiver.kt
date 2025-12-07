package com.example.wordnote.alarm

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.wordnote.data.AppDatabase
import com.example.wordnote.data.AppPreferences
import com.example.wordnote.data.mapper.toData
import com.example.wordnote.domain.model.WordData
import com.example.wordnote.manager.QueueManager
import com.example.wordnote.service.SpeakingService
import com.example.wordnote.utils.NotificationHelper
import com.example.wordnote.utils.WordLevel
import com.example.wordnote.utils.nextTrigger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WordReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        val notificationEnabled = context.isNotificationChannelEnabled("word_channel")
        val wordId = intent.getIntExtra(EXTRA_ID, -1)
        if (wordId == -1) return

        val dao = AppDatabase.getInstance(context).wordDao
        val alarmScheduler = AlarmScheduler(context)

        CoroutineScope(Dispatchers.IO).launch {
            val wordData = dao.getWordById(wordId)?.toData() ?: return@launch

            // Check Quiet Time
            val inQuietTime = isInQuietTime(context)

            val notificationId = wordData.id!!
            val isActive = NotificationHelper.isNotificationActive(context, notificationId)

            //Only show notification when NOT in quiet time
            if (!isActive) {
                NotificationHelper.showWordNotification(context, wordData)
            }

            //Only speak when NOT in quiet time
            if (!inQuietTime && notificationEnabled && AppPreferences.canSpeakingVoiceNotification) {
                QueueManager.add(wordData.word)
                context.startSpeakingService(wordData)
            }

            val newLevel = WordLevel.fromScore(wordData.score)
            val nextTrigger = newLevel.nextTrigger

            dao.updateLevel(wordId, newLevel.ordinal + 1, nextTrigger)
            if (newLevel > WordLevel.LEVEL_4) return@launch

            alarmScheduler.scheduleWord(wordData, nextTrigger)
        }
    }

    companion object {
        const val EXTRA_ID = "ID"
        const val EXTRA_WORD = "WORD"
        const val EXTRA_NOTE = "NOTE"
        const val EXTRA_DEFINITION = "DEFINITION"
    }

    fun Context.startSpeakingService(wordData: WordData) {
        val word = wordData.word
        val note = wordData.note
        val definition = wordData.meanings.firstOrNull()
            ?.definitions?.firstOrNull()
            ?.definition ?: ""

        val intent = Intent(this, SpeakingService::class.java).apply {
            putExtra(EXTRA_WORD, word)
            putExtra(EXTRA_NOTE, note)
            putExtra(EXTRA_DEFINITION, definition)
        }
        ContextCompat.startForegroundService(this, intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun Context.isNotificationChannelEnabled(channelId: String): Boolean {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = manager.getNotificationChannel(channelId)
        return channel != null && channel.importance != NotificationManager.IMPORTANCE_NONE
    }

    private fun isInQuietTime(
        context: Context,
        currentTime: Long = System.currentTimeMillis()
    ): Boolean {
        val dao = AppDatabase.getInstance(context).quiteHourDao
        val quietHours = dao.getAllQuiteHourSync()

        for (range in quietHours) {
            if (currentTime in range.startTime..range.endTime) {
                return true
            }
        }
        return false
    }
}


