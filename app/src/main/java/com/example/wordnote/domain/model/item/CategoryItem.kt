package com.example.wordnote.domain.model.item

data class CategoryItem(
    val title: String,
) {
    companion object {
        fun getList(): List<CategoryItem> {
            return listOf(
                CategoryItem("Category 1"),
            )
        }
    }
}
