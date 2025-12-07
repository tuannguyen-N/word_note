package com.example.wordnote.domain.model

sealed interface SortType{
    object WORD: SortType
    data class LEVEL(val level: Int): SortType
}