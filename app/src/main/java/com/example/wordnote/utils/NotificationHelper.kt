package com.example.wordnote.utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import com.example.wordnote.R
import com.example.wordnote.alarm.WordReplyReceiver
import com.example.wordnote.domain.model.WordData
import com.example.wordnote.ui.activity.main.MainActivity

object NotificationHelper {
    private const val CHANNEL_ID = "word_channel"
    private const val KEY_TEXT_REPLY = "key_text_reply"
    private const val WORD_TEXT = "word_text"
    private const val NOTIFICATION_ID = "notification_id"

    @SuppressLint("MissingPermission")
    fun showWordNotification(context: Context, word: WordData) {
        createChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("WORD_FROM_NOTIFICATION", word.word)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            word.id!!,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        /*RemoteInput for direct reply*/
        val remoteInput = RemoteInput.Builder(KEY_TEXT_REPLY)
            .setLabel("Enter the Word")
            .build()
        val replyIntent = Intent(context, WordReplyReceiver::class.java).apply {
            putExtra("WORD_ID", word.id)
            putExtra(NOTIFICATION_ID, word.id)
        }
        val replyPendingIntent = PendingIntent.getBroadcast(
            context,
            word.id,
            replyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
        val replyAction = NotificationCompat.Action.Builder(
            R.drawable.image_cry,
            "Reply",
            replyPendingIntent
        ).addRemoteInput(remoteInput).build()

        val notificationText: String
        val inboxStyle = NotificationCompat.InboxStyle()

        if (word.note.isNotEmpty()) {
            notificationText = "Note: ${word.note}\nMeaning: ${word.meanings.first().definitions.first().definition}"
        } else {
            val definitions = word.meanings.flatMap { it.definitions }
                .map { it.definition }
                .take(2)
            definitions.forEach { inboxStyle.addLine(it) }
            notificationText = definitions.firstOrNull() ?: ""
        }
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("What's this word ?")
            .setContentText(notificationText)
            .setSmallIcon(R.drawable.image_cry)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.image_cry))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(replyAction)

        val nm = NotificationManagerCompat.from(context)
        nm.notify(word.id, builder.build())
    }

    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Word Notification",
                NotificationManager.IMPORTANCE_HIGH
            )
            val nm = context.getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(channel)
        }
    }

    fun isNotificationActive(context: Context, notificationId: Int): Boolean {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val active = nm.activeNotifications ?: return false
        return active.any { it.id == notificationId }
    }
}