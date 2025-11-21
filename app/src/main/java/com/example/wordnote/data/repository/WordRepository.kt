package com.example.wordnote.data.repository

import com.example.wordnote.data.api.WordAPI
import com.example.wordnote.data.dao.WordCategoryCrossRefDao
import com.example.wordnote.data.dao.WordDao
import com.example.wordnote.data.entities.WordCategoryCrossRef
import com.example.wordnote.data.mapper.toData
import com.example.wordnote.data.mapper.toEntity
import com.example.wordnote.domain.model.WordData
import com.example.wordnote.util.Result
import com.example.wordnote.util.WordLevel
import com.example.wordnote.util.getDelay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.collections.map

class WordRepository(
    private val dao: WordDao,
    private val wordCategoryDao: WordCategoryCrossRefDao? = null,
    private val api: WordAPI? = null
) {
    suspend fun upsertWord(rawWord: String, level: Int, categoryId: Int?): Result {
        return try {
            val word = rawWord.trim().lowercase()
            val existingWord = dao.getWord(word)

            if (existingWord != null) {
                if (categoryId != null && wordCategoryDao!!.isWordInCategory(existingWord.id, categoryId) > 0)
                    return Result.AlreadyExists

                val categories = wordCategoryDao!!.getCategoriesOfWord(existingWord.id)

                return Result.AlreadyExistsInCategories(
                    word = existingWord.toData(),
                    categoryNames = categories.map { it.name }
                )
            }

            val response = api!!.getWordMeaning(word)
            if (response.isEmpty()) {
                return Result.NotFound
            }

            val data = response.first().toData()
                .copy(level = level, addedTime = System.currentTimeMillis())

            val wordId = dao.upsertWord(data.toEntity()).toInt()

            categoryId?.let {
                wordCategoryDao!!.insert(
                    WordCategoryCrossRef(wordId, categoryId)
                )
            }
            Result.Success(data.copy(id = wordId))

        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun deleteWord(word: WordData) {
        dao.deleteWord(word.id!!)
    }

    suspend fun updateLevel(word: WordData) {
        val nextTriggerTime = System.currentTimeMillis() + getDelay(word.level)
        dao.updateLevel(word.id!!, word.level, nextTriggerTime)
    }

    suspend fun updateNote(word: WordData) {
        dao.updateNote(word.id!!, word.note)
    }

    fun getWordsOrderedByWord(): Flow<List<WordData>> =
        dao.getWordsOrderedByWord().map { list -> list.map { it.toData() } }

    fun getWordsByLevel(level: Int): Flow<List<WordData>> =
        dao.getWordsByLevel(level).map { list -> list.map { it.toData() } }

    fun getWordsByCategory(id: Int): Flow<List<WordData>> =
        dao.getWordsByCategory(id).map { list -> list.map { it.toData() } }

    fun getWordsByCategoryAndLevel(id: Int, level: Int): Flow<List<WordData>> =
        dao.getWordsByCategoryAndLevel(id, level).map { list -> list.map { it.toData() } }
}