package com.example.wordnote.alarm

import android.app.NotificationManager
import android.app.RemoteInput
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.wordnote.data.AppDatabase
import com.example.wordnote.data.repository.WordRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WordReplyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        val wordRepository = WordRepository(AppDatabase.getInstance(context).wordDao)

        val results = RemoteInput.getResultsFromIntent(intent)
        val replyText = results?.getCharSequence("key_text_reply")?.toString()?.trim()
        val wordId = intent.getIntExtra("WORD_ID", -1)
        val notificationId = intent.getIntExtra("notification_id", -1)

        if (replyText.isNullOrEmpty() || wordId == -1) return
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            val word = wordRepository.getWordById(wordId)
            var delta = 0

            if (word.word.equals(replyText, ignoreCase = true)) {
                delta = 1
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Correct!", Toast.LENGTH_SHORT).show()
                }
            } else {
                delta = -1
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Wrong!", Toast.LENGTH_SHORT).show()
                }
            }

            val newScore = maxOf(0, word.score + delta)
            wordRepository.updateScore(word.id!!, newScore)
            if (notificationId != -1) {
                val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.cancel(notificationId)
            }

            pendingResult.finish()
        }
    }
}
