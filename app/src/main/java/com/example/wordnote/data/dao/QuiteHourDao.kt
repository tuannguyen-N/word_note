package com.example.wordnote.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.wordnote.data.entities.QuiteHourEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuiteHourDao {
    @Insert
    suspend fun insertQuiteHour(quiteHourEntity: QuiteHourEntity)

    @Query("SELECT * FROM QuiteHourEntity")
    suspend fun getAllQuiteHour(): Flow<List<QuiteHourEntity>>

    @Delete
    suspend fun deleteQuiteHour(id: Int)
}