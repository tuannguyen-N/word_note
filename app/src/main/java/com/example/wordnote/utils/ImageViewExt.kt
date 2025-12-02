package com.example.wordnote.utils

import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import javax.sql.DataSource

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

fun ImageView.loadGlideImage(model: Any){
    Glide.with(context)
        .load(model)
        .into(this)
}

fun ImageView.loadGif(model: Any, onLoaded: (com.bumptech.glide.load.resource.gif.GifDrawable?) -> Unit = {}) {
    Glide.with(context)
        .asGif()
        .load(model)
        .listener(object : RequestListener<GifDrawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: com.bumptech.glide.request.target.Target<GifDrawable?>?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

            override fun onResourceReady(
                resource: GifDrawable?,
                model: Any?,
                target: com.bumptech.glide.request.target.Target<GifDrawable?>?,
                dataSource: com.bumptech.glide.load.DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }
        })
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