package com.example.wordnote.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.wordnote.data.dao.WordDao
import com.example.wordnote.data.entities.WordEntity

@Database(
    entities = [WordEntity::class],
    version = 1
)
abstract class WordDatabase: RoomDatabase() {
    abstract val dao: WordDao
}

