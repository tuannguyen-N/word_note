package com.example.wordnote.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.wordnote.data.dao.WordDao
import com.example.wordnote.data.entities.WordEntity

@Database(
    entities = [WordEntity::class],
    version = 4
)
abstract class WordDatabase : RoomDatabase() {
    abstract val dao: WordDao

    companion object {
        @Volatile
        private var INSTANCE: WordDatabase? = null

        fun getInstance(context: Context): WordDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WordDatabase::class.java,
                    "word_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}