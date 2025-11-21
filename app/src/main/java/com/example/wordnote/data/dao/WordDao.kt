package com.example.wordnote.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.wordnote.data.entities.WordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {

    @Query("SELECT * FROM WORDENTITY WHERE WORD = :word LIMIT 1")
    suspend fun getWord(word: String): WordEntity?

    @Query("SELECT * FROM wordentity WHERE id = :id")
    suspend fun getWordById(id: Int): WordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertWord(word: WordEntity): Long

    @Query("DELETE FROM wordentity WHERE id = :wordId")
    suspend fun deleteWord(wordId: Int)

    @Query("UPDATE wordentity SET level = :level, nextTriggerTime =:nextTriggerTime WHERE id = :wordId")
    suspend fun updateLevel(wordId: Int, level: Int, nextTriggerTime: Long)

    @Query("UPDATE wordentity SET note = :note WHERE id = :wordId")
    suspend fun updateNote(wordId: Int, note: String)

    @Query("SELECT changes()")
    suspend fun getChangedRowCount(): Int

    @Query("SELECT * FROM wordentity ORDER BY word ASC")
    fun getWordsOrderedByWord(): Flow<List<WordEntity>>

    @Query("SELECT * FROM WordEntity ORDER BY word ASC")
    suspend fun getAllWordsSync(): List<WordEntity>

    @Query("SELECT * FROM wordentity WHERE level = :level")
    fun getWordsByLevel(level: Int): Flow<List<WordEntity>>

    @Transaction
    @Query("""
    SELECT WordEntity.* FROM WordEntity
    INNER JOIN WordCategoryCrossRef ON WordEntity.id = WordCategoryCrossRef.wordId
    WHERE WordCategoryCrossRef.categoryId = :categoryId
""")
    fun getWordsByCategory(categoryId: Int): Flow<List<WordEntity>>

    @Transaction
    @Query("""
    SELECT WordEntity.* FROM WordEntity
    INNER JOIN WordCategoryCrossRef ON WordEntity.id = WordCategoryCrossRef.wordId
    WHERE WordCategoryCrossRef.categoryId = :categoryId AND WordEntity.level = :level
""")
    fun getWordsByCategoryAndLevel(categoryId: Int, level: Int): Flow<List<WordEntity>>
}