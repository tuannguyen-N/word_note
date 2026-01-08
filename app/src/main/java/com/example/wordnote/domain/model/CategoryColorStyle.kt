package com.example.wordnote.domain.model

import androidx.annotation.ColorRes

data class CategoryColorStyle(
    @ColorRes val background: Int,
    @ColorRes val textColor: Int,
    @ColorRes val dividerColor: Int
)