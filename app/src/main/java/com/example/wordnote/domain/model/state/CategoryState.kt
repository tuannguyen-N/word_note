package com.example.wordnote.domain.model.state

import com.example.wordnote.domain.model.CategoryData

data class CategoryState(
    val categories: List<CategoryData> = emptyList(),
    val isLoading: Boolean = false
)