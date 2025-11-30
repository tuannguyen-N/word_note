package com.example.wordnote.alarm

import android.app.NotificationManager
import android.app.RemoteInput
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.wordnote.data.AppDatabase
import com.example.wordnote.data.repository.WordRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WordReplyReceiver(
) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        val wordRepository = WordRepository(AppDatabase.getInstance(context).wordDao)

        val results = RemoteInput.getResultsFromIntent(intent)
        val replyText = results?.getCharSequence("key_text_reply")?.toString()?.trim()
        val wordId = intent.getIntExtra("WORD_ID", -1)
        val notificationId = intent.getIntExtra("notification_id", -1)

        if (replyText.isNullOrEmpty() || wordId == -1) return

        CoroutineScope(Dispatchers.IO).launch {
            val word = wordRepository.getWordById(wordId)
            val delta = if (word.word.equals(replyText, ignoreCase = true)) 1 else -1
            val newScore = maxOf(0, word.score + delta)
            wordRepository.updateScore(word.id!!, newScore)
        }

        if (notificationId != -1) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.cancel(notificationId)
        }
    }
}
