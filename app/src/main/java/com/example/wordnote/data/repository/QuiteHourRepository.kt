package com.example.wordnote.data.repository

import com.example.wordnote.data.dao.QuiteHourDao
import com.example.wordnote.data.entities.QuiteHourEntity
import kotlinx.coroutines.flow.Flow

class QuiteHourRepository(
    private val quiteHourDao: QuiteHourDao
) {
    suspend fun insertQuiteHour(startTime: Long, endTime: Long) {
        quiteHourDao.insertQuiteHour(QuiteHourEntity(startTime = startTime, endTime = endTime))
    }

    suspend fun deleteQuiteHour(id: Int) {
        quiteHourDao.deleteQuiteHour(id)
    }

    fun getAllQuiteHour(): Flow<List<QuiteHourEntity>> {
        return quiteHourDao.getAllQuiteHour()
    }
}