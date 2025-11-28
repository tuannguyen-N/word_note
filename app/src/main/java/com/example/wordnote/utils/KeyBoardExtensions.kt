package com.example.wordnote.utils

import android.view.View
import android.view.ViewGroup
import androidx.core.view.*
import androidx.core.view.*

fun View.followKeyboard(target: View) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { _, insets ->
        val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom

        target.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            bottomMargin = imeHeight
        }
        insets
    }
}
