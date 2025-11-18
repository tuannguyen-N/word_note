package com.example.wordnote.util

sealed interface SortType{
    object WORD: SortType
    data class LEVEL(val level: Int): SortType
}