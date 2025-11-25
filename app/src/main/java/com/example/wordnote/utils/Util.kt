package com.example.wordnote.utils

import android.animation.ObjectAnimator
import android.view.View

fun shakeView(view: View){
    val shake = ObjectAnimator.ofFloat(view, "translationX", 0f, 25f, -25f, 15f, -15f, 6f, -6f, 0f)
    shake.duration = 500
    shake.start()
}
