package com.example.wordnote.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class QuiteHourEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int =0 ,
    val startHour: Int,
    val endHour: Int
)