package com.example.wordnote.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.example.wordnote.R
import com.example.wordnote.utils.QueueManager
import com.example.wordnote.utils.SpeakingManager

class SpeakingService : Service() {
    private lateinit var speakingManager: SpeakingManager
    private var isTtsReady = false

    override fun onCreate() {
        super.onCreate()
        speakingManager = SpeakingManager(this) {
            isTtsReady = true
            startNext()
        }
        speakingManager.onDoneCallback = {
            startNext()
        }
        startForeground(1, buildSilentNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val word = intent?.getStringExtra("WORD")

        if (word != null) {
            QueueManager.add(word)
            if (isTtsReady) {
                Handler(Looper.getMainLooper()).postDelayed({
                    startNext()
                }, 500)
            }
        }
        return START_NOT_STICKY
    }

    private fun startNext() {
        val next = QueueManager.next()
        if (next != null) {
            speakingManager.speak(next)
        } else {
            stopSelf()
        }
    }

    private fun buildSilentNotification(): Notification {
        return NotificationCompat.Builder(this, "word_channel")
            .setSmallIcon(R.drawable.icon_notification)
            .setContentTitle("Reading word…")
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .build()
    }

    override fun onDestroy() {
        speakingManager.destroy()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
