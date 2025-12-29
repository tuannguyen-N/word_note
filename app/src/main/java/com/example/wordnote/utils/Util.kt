package com.example.wordnote.utils

import android.animation.ObjectAnimator
import android.content.Context
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.example.wordnote.R
import com.example.wordnote.domain.model.CategoryColorStyle
import com.example.wordnote.domain.model.ColorType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

fun Int.toDp(context: Context): Int =
    (this * context.resources.displayMetrics.density).toInt()

fun ColorType.toStyle(): CategoryColorStyle{
    return when(this){
        ColorType.NORMAL -> CategoryColorStyle(
            background = R.color.white,
            textColor = R.color.black
        )
        ColorType.PURPLE -> CategoryColorStyle(
            background = R.color.purple,
            textColor = R.color.text_purple
        )
        ColorType.YELLOW -> CategoryColorStyle(
            background = R.color.orange,
            textColor = R.color.text_orange
        )
        ColorType.GREEN -> CategoryColorStyle(
            background = R.color.green,
            textColor = R.color.text_green
        )
        ColorType.BLUE -> CategoryColorStyle(
            background = R.color.blue,
            textColor = R.color.text_blue
        )
        ColorType.PIG -> CategoryColorStyle(
            background = R.color.pink,
            textColor = R.color.text_pink
        )
    }
}

fun View.color(@ColorRes res: Int): Int =
    ContextCompat.getColor(context, res)


fun View.setSafeOnClickListener(
    debounceTime: Long = 500L,
    scope: CoroutineScope = MainScope(),
    onClick: () -> Unit
) {
    var job: Job? = null

    setOnClickListener {
        if (job?.isActive == true) return@setOnClickListener

        job = scope.launch {
            onClick()
            delay(debounceTime)
        }
    }
}