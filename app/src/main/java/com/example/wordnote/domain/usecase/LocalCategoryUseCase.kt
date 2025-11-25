package com.example.wordnote.domain.usecase

import com.example.wordnote.data.repository.CategoryRepository
import com.example.wordnote.domain.model.CategoryData
import com.example.wordnote.utils.Result
import kotlinx.coroutines.flow.Flow

class LocalCategoryUseCase(
    private val categoryRepository: CategoryRepository
) {
    suspend fun insertCategory(name: String, description: String): Result =
        categoryRepository.insetCategory(name.lowercase(), description)

    suspend fun deleteCategory(id: Int) {
        categoryRepository.deleteCategory(id)
    }

    suspend fun updateCategory(id: Int, name: String, description: String): Result =
        categoryRepository.updateCategory(id, name.lowercase(), description)

    fun getCategories(): Flow<List<CategoryData>> = categoryRepository.getCategories()
    fun getCategoriesWithWordLevel(): Flow<List<CategoryData>> = categoryRepository.getCategoriesWithWordLevel()
}