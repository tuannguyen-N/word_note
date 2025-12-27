package com.example.wordnote.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.wordnote.data.dao.CategoryDao
import com.example.wordnote.data.dao.QuiteHourDao
import com.example.wordnote.data.dao.WordCategoryCrossRefDao
import com.example.wordnote.data.dao.WordDao
import com.example.wordnote.data.entities.CategoryEntity
import com.example.wordnote.data.entities.QuiteHourEntity
import com.example.wordnote.data.entities.WordCategoryCrossRef
import com.example.wordnote.data.entities.WordEntity
import com.example.wordnote.utils.TypeConverter

@Database(
    entities = [
        WordEntity::class,
        CategoryEntity::class,
        WordCategoryCrossRef::class,
        QuiteHourEntity::class],
    version = 1
)
@TypeConverters(TypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val wordDao: WordDao
    abstract val categoryDao: CategoryDao
    abstract val wordCategoryDao: WordCategoryCrossRefDao
    abstract val quiteHourDao: QuiteHourDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addMigrations()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}