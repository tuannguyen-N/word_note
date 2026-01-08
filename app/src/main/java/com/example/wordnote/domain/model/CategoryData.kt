package com.example.wordnote.domain.model

data class CategoryData(
    val id: Int? = null,
    val name: String = "",
    val description: String ="",
//    val numberWordLevel1: Int = 0,
//    val numberWordLevel2: Int = 0,
//    val numberWordLevel3: Int = 0,
    val createAt: Long = 0,
    val previewWords: List<String> = emptyList(),
    val color: ColorType = ColorType.NORMAL,
    val isFavorite: Boolean = false,
    var wordNumber: Int = 0
)