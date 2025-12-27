package com.example.wordnote.ui.fragment.category

sealed interface CategoryAction {
    data class OnSaveCategory(val name: String, val description: String) : CategoryAction
    data class OnDeleteSelectedList(val selectedIds: List<Int>) : CategoryAction
    data class OnEditCategory(
        val id: Int,
        val name: String,
        val description: String
    ) : CategoryAction
}