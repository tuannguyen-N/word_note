package com.example.wordnote.domain.model

sealed interface Result {
    data class Success(val word: WordData? = null) : Result
    object NotFound : Result
    object AlreadyExists : Result
    data class Error(val message: String) : Result
    data class AlreadyExistsInCategories(
        val word: WordData,
        val category: CategoryData
    ) : Result
}