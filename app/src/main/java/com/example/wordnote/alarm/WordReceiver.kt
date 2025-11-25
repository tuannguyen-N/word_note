package com.example.wordnote.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.example.wordnote.data.AppDatabase
import com.example.wordnote.data.AppPreferences
import com.example.wordnote.data.mapper.toData
import com.example.wordnote.manager.WordLevelManager
import com.example.wordnote.service.SpeakingService
import com.example.wordnote.utils.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WordReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (!AppPreferences.canPostNotifications) return

        val wordId = intent.getIntExtra(EXTRA_ID, -1)
        if (wordId == -1) return
        val dao = AppDatabase.getInstance(context).wordDao
        val levelManager = WordLevelManager()

        CoroutineScope(Dispatchers.IO).launch {
            val wordData = dao.getWordById(wordId)?.toData() ?: return@launch
            val word = wordData.word
            val note = wordData.note
            val definition = wordData.meanings.firstOrNull()
                ?.definitions?.firstOrNull()
                ?.definition ?: ""

            withContext(Dispatchers.Main) {
                val notificationId = wordData.id!!
                val stillActive = NotificationHelper.isNotificationActive(context, notificationId)
                if (!stillActive) {
                    NotificationHelper.showWordNotification(context, wordData)
                }
                context.startSpeakingService(word, note, definition)
            }

            val newLevel = levelManager.calculateLevelFromScore(wordData.score)
            val nextTrigger = levelManager.nextTriggerTime(newLevel)
            dao.updateLevel(wordId, newLevel.ordinal + 1, nextTrigger)
            AlarmScheduler(context).scheduleWord(
                wordData,
                nextTrigger
            )
        }
    }

    companion object {
        const val EXTRA_ID = "ID"
        const val EXTRA_WORD = "WORD"
        const val EXTRA_NOTE = "NOTE"
        const val EXTRA_DEFINITION = "DEFINITION"
        const val EXTRA_LEVEL = "LEVEL"
    }

    fun Context.startSpeakingService(word: String, note: String?, definition: String) {
        val intent = Intent(this, SpeakingService::class.java).apply {
            putExtra(EXTRA_WORD, word)
            putExtra(EXTRA_NOTE, note)
            putExtra(EXTRA_DEFINITION, definition)
        }
        ContextCompat.startForegroundService(this, intent)
    }
}
