package com.example.wordnote.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.wordnote.domain.model.ColorType

@Entity
data class CategoryEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String = "",
    val color: ColorType = ColorType.NORMAL,
    val isFavorite: Boolean = false
)