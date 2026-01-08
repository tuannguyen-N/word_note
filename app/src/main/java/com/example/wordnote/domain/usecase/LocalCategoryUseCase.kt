package com.example.wordnote.domain.usecase

import com.example.wordnote.data.repository.CategoryRepository
import com.example.wordnote.domain.model.CategoryData
import com.example.wordnote.domain.model.Result
import kotlinx.coroutines.flow.Flow

class LocalCategoryUseCase(
    private val categoryRepository: CategoryRepository
) {
    suspend fun insertCategory(name: String, description: String): Result =
        categoryRepository.insetCategory(name.lowercase(), description)

    suspend fun deleteCategory(id: Int) {
        categoryRepository.deleteCategory(id)
    }

    suspend fun countWordsByCategory(id: Int): Int {
        return categoryRepository.countWordsByCategory(id)
    }

    suspend fun getCategory(id: Int): CategoryData {
        return categoryRepository.getCategory(id)
    }

    suspend fun deleteCategories(selectedIds: List<Int>) {
        categoryRepository.deleteCategories(selectedIds)
    }

    suspend fun updateCategory(id: Int, name: String, description: String): Result =
        categoryRepository.updateCategory(id, name.lowercase(), description)

    suspend fun getPreviewWords(categoryIds: List<Int>): Map<Int, List<String>> {
        return categoryIds.associateWith { id ->
            categoryRepository.getWordsByCategoryId(id)
                .shuffled()
                .take((5..15).random())
        }
    }

    suspend fun toggleFavorite(id: Int) {
        categoryRepository.toggleFavorite(id)
    }

    fun getCategories(): Flow<List<CategoryData>> = categoryRepository.getCategories()
//    fun getCategoriesWithWordLevel(): Flow<List<CategoryData>> = categoryRepository.getCategoriesWithWordLevel()
}