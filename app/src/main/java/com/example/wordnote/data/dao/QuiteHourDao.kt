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
    fun getAllQuiteHour(): Flow<List<QuiteHourEntity>>

    @Query("DELETE FROM QuiteHourEntity WHERE id = :id")
    suspend fun deleteQuiteHour(id: Int)

    @Query("SELECT * FROM QUITEHOURENTITY")
    fun getAllQuiteHourSync(): List<QuiteHourEntity>
}