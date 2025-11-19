package com.example.wordnote.data.mapper

import androidx.room.TypeConverter
import com.example.wordnote.domain.model.MeaningData
import com.google.gson.Gson

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromMeanings(meanings: List<MeaningData>): String = gson.toJson(meanings)

    @TypeConverter
    fun toMeanings(data: String): List<MeaningData> =
        gson.fromJson(data, Array<MeaningData>::class.java).toList()
}