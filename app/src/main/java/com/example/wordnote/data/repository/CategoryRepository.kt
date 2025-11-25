package com.example.wordnote.data.repository

import com.example.wordnote.data.dao.CategoryDao
import com.example.wordnote.data.entities.CategoryEntity
import com.example.wordnote.data.mapper.toData
import com.example.wordnote.domain.model.CategoryData
import com.example.wordnote.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepository(
    private val categoryDao: CategoryDao
) {
    suspend fun insetCategory(name: String, description: String): Result {
        if (categoryDao.getCategoryByName(name) != null)
            return Result.AlreadyExists
        categoryDao.insertCategory(
            CategoryEntity(
                name = name,
                description = description
            )
        )
        return Result.Success()
    }

    suspend fun deleteCategory(id: Int) {
        categoryDao.deleteCategory(id)
    }

    suspend fun updateCategory(id: Int, name: String, description: String): Result {
        if (categoryDao.getCategoryByName(name)!= null) return Result.AlreadyExists
        categoryDao.updateCategory(id, name, description)
        return Result.Success()
    }

    fun getCategories(): Flow<List<CategoryData>> {
        return categoryDao.getCategories().map { list -> list.map { it.toData() } }
    }

    fun getCategoriesWithWordLevel(): Flow<List<CategoryData>> = categoryDao.getCategoriesWithWordCount()
}