package com.example.wordnote.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.example.wordnote.data.AppDatabase
import com.example.wordnote.data.AppPreferences
import com.example.wordnote.data.mapper.toData
import com.example.wordnote.domain.model.WordData
import com.example.wordnote.service.SpeakingService
import com.example.wordnote.util.NotificationHelper
import com.example.wordnote.util.nextTrigger
import com.example.wordnote.util.toWordLevelOrNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WordReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (!AppPreferences.canPostNotifications) return

        val wordId = intent.getIntExtra(EXTRA_ID, -1)
        val word = intent.getStringExtra(EXTRA_WORD) ?: return
        val note = intent.getStringExtra(EXTRA_NOTE)
        val definition = intent.getStringExtra(EXTRA_DEFINITION) ?: return
        val level = intent.getIntExtra(EXTRA_LEVEL, -1)
        val level1 = level.toWordLevelOrNull() ?: return

        NotificationHelper.showWordNotification(context, word, note, definition)

        val dao = AppDatabase.getInstance(context).wordDao

        CoroutineScope(Dispatchers.IO).launch {
            val delay = level1.delayMillis
            val next = System.currentTimeMillis() + delay

            dao.updateLevel(wordId, level, next)

            AlarmScheduler(context).scheduleWord(
                dao.getWordById(wordId)!!.toData(),
                next
            )
        }

        context.startSpeakingService(word, note, definition)
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
