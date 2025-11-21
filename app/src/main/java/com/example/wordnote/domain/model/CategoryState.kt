package com.example.wordnote.domain.model

data class CategoryState(
    val categories: List<CategoryData> = emptyList(),
    val isLoading: Boolean = false
)