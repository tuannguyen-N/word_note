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

    @Query("UPDATE CATEGORYENTITY SET NAME = :newName, DESCRIPTION = :newDescription WHERE ID = :categoryId")
    suspend fun updateCategory(categoryId: Int, newName: String, newDescription: String)

    @Query("SELECT * FROM categoryentity ORDER BY name ASC")
    fun getCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categoryentity WHERE name = :name")
    suspend fun getCategoryByName(name: String): CategoryEntity?

    @Query("""
    SELECT 
        c.id AS id,
        c.name AS name,
        c.description AS description,

        SUM(CASE WHEN w.level = 1 THEN 1 ELSE 0 END) AS numberWordLevel1,
        SUM(CASE WHEN w.level = 2 THEN 1 ELSE 0 END) AS numberWordLevel2,
        SUM(CASE WHEN w.level = 3 THEN 1 ELSE 0 END) AS numberWordLevel3

    FROM CategoryEntity c
    LEFT JOIN WordCategoryCrossRef r ON c.id = r.categoryId
    LEFT JOIN WordEntity w ON w.id = r.wordId

    GROUP BY c.id
    ORDER BY c.name ASC
""")
    fun getCategoriesWithWordCount(): Flow<List<CategoryData>>

}