package com.example.wordnote.domain.model

import com.example.wordnote.util.SortType

data class WordState (
    val words: List<WordData> = emptyList(),
    val sortType: SortType = SortType.WORD,
    val isLoading: Boolean = false,
    val selectedLevel: Int?  = null
)