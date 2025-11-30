package com.example.wordnote.utils

import android.graphics.Insets
import android.view.View
import android.view.ViewGroup
import androidx.core.view.*
import androidx.core.view.*

fun View.followKeyboardAndEdge(
    target: View,
    topCover: View? = null,
    bottomCover: View? = null,
) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { _, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom

        topCover?.updateLayoutParams { height = systemBars.top }
//        bottomCover?.updateLayoutParams { height = navigationBars.bottom }

//        view.updatePadding(top = systemBars.top, bottom = navigationBars.bottom)

        target.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            bottomMargin = imeHeight
        }

        insets
    }
}
fun View.followKeyboard(target: View) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { _, insets ->
        val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom

        target.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            bottomMargin = imeHeight
        }
        insets
    }
}
