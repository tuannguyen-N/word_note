package com.example.wordnote.domain.model.state

import com.example.wordnote.domain.model.SortType
import com.example.wordnote.domain.model.WordData

data class WordState (
    val words: List<WordData> = emptyList(),
    val sortType: SortType = SortType.WORD,
    val isLoading: Boolean = false,
    val selectedLevel: Int?  = null
)