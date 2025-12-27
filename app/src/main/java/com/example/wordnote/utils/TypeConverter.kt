package com.example.wordnote.utils

import androidx.room.TypeConverter
import com.example.wordnote.domain.model.ColorType

class TypeConverter {

    @TypeConverter
    fun fromColorType(colorType: ColorType): Int {
        return colorType.color
    }

    @TypeConverter
    fun toColorType(value: Int): ColorType {
        return ColorType.entries.firstOrNull { it.color == value }
            ?: ColorType.NORMAL
    }
}
