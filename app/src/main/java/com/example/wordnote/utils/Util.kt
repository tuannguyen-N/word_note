package com.example.wordnote.utils

import android.animation.ObjectAnimator
import android.view.View

object Utils {
    fun shakeView(view: View){
        val shake = ObjectAnimator.ofFloat(view, "translationX", 0f, 25f, -25f, 15f, -15f, 6f, -6f, 0f)
        shake.duration = 500
        shake.start()
    }
    fun formatTime(hour: Int, minute: Int): String {
        return String.format("%02d:%02d", hour, minute)
    }
    fun formatTimeMinutes(totalMinutes: Int): String {
        val hour = totalMinutes / 60
        val minute = totalMinutes % 60
        return formatTime(hour, minute)
    }
}
