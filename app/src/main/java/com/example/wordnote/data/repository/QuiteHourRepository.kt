package com.example.wordnote.data.repository

import com.example.wordnote.data.dao.QuiteHourDao
import com.example.wordnote.data.entities.QuiteHourEntity
import kotlinx.coroutines.flow.Flow

class QuiteHourRepository(
    private val quiteHourDao: QuiteHourDao
) {
    suspend fun insertQuiteHour(quiteHourEntity: QuiteHourEntity) {
        quiteHourDao.insertQuiteHour(quiteHourEntity)
    }

    suspend fun deleteQuiteHour(id: Int) {
        quiteHourDao.deleteQuiteHour(id)
    }

    suspend fun getAllQuiteHour(): Flow<List<QuiteHourEntity>> {
        return quiteHourDao.getAllQuiteHour()
    }
}