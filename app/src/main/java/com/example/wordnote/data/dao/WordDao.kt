package com.example.wordnote.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.example.wordnote.data.entities.WordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {

    @Query("SELECT * FROM WORDENTITY WHERE WORD = :word LIMIT 1")
    suspend fun getWord(word: String): WordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertWord(word: WordEntity)

    @Query("DELETE FROM wordentity WHERE word = :word")
    suspend fun deleteWord(word: String)

    @Query("UPDATE wordentity SET level = :level WHERE word = :word")
    suspend fun updateLevel(word: String, level: Int)

    @Query("UPDATE wordentity SET note = :note WHERE word = :word")
    suspend fun updateNote(word: String, note: String)

    @Query("SELECT changes()")
    suspend fun getChangedRowCount(): Int

    @Query("SELECT * FROM wordentity ORDER BY word ASC")
    fun getWordsOrderedByWord(): Flow<List<WordEntity>>

    @Query("SELECT * FROM wordentity WHERE level = :level")
    fun getWordsByLevel(level: Int): Flow<List<WordEntity>>
}