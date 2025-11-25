package com.example.wordnote.utils

sealed interface SortType{
    object WORD: SortType
    data class LEVEL(val level: Int): SortType
}