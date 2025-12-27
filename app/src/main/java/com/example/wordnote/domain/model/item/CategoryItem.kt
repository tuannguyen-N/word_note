package com.example.wordnote.domain.model.item

import com.example.wordnote.domain.model.CategoryData

sealed interface CategoryItem{
    data class Data( val data: CategoryData): CategoryItem
    object Add: CategoryItem
}
