package com.example.wordnote.utils

import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide

fun AppCompatImageView.loadGlideImage(model: Any) {
    Glide.with(context)
        .load(model)
        .into(this)
}

fun AppCompatImageView.load0GlideImage(model: Any) {
    Glide.with(context)
        .load(model)
        .frame(0)
        .into(this)
}

fun AppCompatImageView.animateUp() {
    visibility = View.VISIBLE
    translationY = height.toFloat()
    alpha = 0f
    animate()
        .translationY(0f)
        .alpha(1f)
        .setDuration(300)
        .setInterpolator(DecelerateInterpolator())
        .start()
}

fun AppCompatImageView.animateDown() {
    this.animate()
        .translationY(height.toFloat())
        .alpha(0f)
        .setDuration(300)
        .setInterpolator(AccelerateInterpolator())
        .withEndAction {
            visibility = View.GONE
        }
        .start()
}