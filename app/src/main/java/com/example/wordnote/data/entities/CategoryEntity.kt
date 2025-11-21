package com.example.wordnote.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CategoryEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String = ""
)