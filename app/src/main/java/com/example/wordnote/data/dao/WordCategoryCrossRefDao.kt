package com.example.wordnote.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.wordnote.data.entities.CategoryEntity
import com.example.wordnote.data.entities.WordCategoryCrossRef

@Dao
interface WordCategoryCrossRefDao{
    @Insert
    suspend fun insert(wordCategoryCrossRef: WordCategoryCrossRef)

    @Delete
    suspend fun delete(wordCategoryCrossRef: WordCategoryCrossRef)

    @Query("""
        SELECT COUNT(*) FROM WordCategoryCrossRef 
        WHERE wordId = :wordId AND categoryId = :categoryId
    """)
    suspend fun isWordInCategory(wordId: Int, categoryId: Int): Int

    @Query("""
    SELECT CategoryEntity.* FROM CategoryEntity
    INNER JOIN WordCategoryCrossRef 
        ON CategoryEntity.id = WordCategoryCrossRef.categoryId
    WHERE WordCategoryCrossRef.wordId = :wordId
""")
    suspend fun getCategoriesOfWord(wordId: Int): CategoryEntity

}