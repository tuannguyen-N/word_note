package com.example.wordnote.ui.fragment.category

sealed interface CategoryUIEvent {
    data class ScrollToExistCategory(val name: String): CategoryUIEvent
}