package com.example.wordnote.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.wordnote.data.entities.CategoryEntity
import com.example.wordnote.domain.model.CategoryData
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(categoryEntity: CategoryEntity)

    @Query("DELETE FROM categoryentity WHERE id = :categoryId")
    suspend fun deleteCategory(categoryId: Int)

    @Query("SELECT * FROM categoryentity WHERE id = :categoryId")
    suspend fun getCategory(categoryId: Int): CategoryEntity

    @Query("UPDATE CATEGORYENTITY SET NAME = :newName, DESCRIPTION = :newDescription WHERE ID = :categoryId")
    suspend fun updateCategory(categoryId: Int, newName: String, newDescription: String)

    @Query("SELECT * FROM categoryentity ORDER BY isFavorite DESC")
    fun getCategories(): Flow<List<CategoryEntity>>

    @Query("DELETE FROM WordEntity WHERE id IN (SELECT wordId FROM WordCategoryCrossRef WHERE categoryId = :categoryId)")
    suspend fun deleteWordsByCategory(categoryId: Int)

    @Query("DELETE FROM WordEntity WHERE id IN (SELECT wordId FROM WordCategoryCrossRef WHERE categoryId IN (:categoryIds))")
    suspend fun deleteWordsByCategories(categoryIds: List<Int>)

    @Query("DELETE FROM categoryentity WHERE id IN (:categoryIds)")
    suspend fun deleteCategories(categoryIds: List<Int>)

    @Query("SELECT * FROM categoryentity WHERE name = :name")
    suspend fun getCategoryByName(name: String): CategoryEntity?

    @Query("UPDATE categoryentity SET isFavorite = NOT isFavorite WHERE id = :id")
    suspend fun toggleFavorite(id: Int)

    @Query("""
    SELECT w.word
    FROM WordEntity w
    INNER JOIN WordCategoryCrossRef r ON w.id = r.wordId
    WHERE r.categoryId = :categoryId
""")
    suspend fun getWordsByCategoryId(categoryId: Int): List<String>

    @Query("SELECT COUNT(*) FROM WordEntity WHERE id IN (SELECT wordId FROM WordCategoryCrossRef WHERE categoryId = :categoryId)")
    suspend fun countWordsByCategory(categoryId: Int): Int

//    @Query(
//        """
//    SELECT
//        c.id AS id,
//        c.name AS name,
//        c.description AS description,
//        c.color AS color,
//
//        COALESCE(SUM(CASE WHEN w.level = 1 THEN 1 ELSE 0 END), 0) AS numberWordLevel1,
//        COALESCE(SUM(CASE WHEN w.level = 2 THEN 1 ELSE 0 END), 0) AS numberWordLevel2,
//        COALESCE(SUM(CASE WHEN w.level = 3 THEN 1 ELSE 0 END), 0) AS numberWordLevel3
//
//    FROM CategoryEntity c
//    LEFT JOIN WordCategoryCrossRef r ON c.id = r.categoryId
//    LEFT JOIN WordEntity w ON w.id = r.wordId
//
//    GROUP BY c.id, c.name, c.description, c.color
//    ORDER BY c.name ASC
//"""
//    )
//    fun getCategoriesWithWordCount(): Flow<List<CategoryData>>
}