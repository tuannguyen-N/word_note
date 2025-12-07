package com.example.wordnote.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class QuiteHourEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int =0 ,
    val startTime: Long,
    val endTime: Long
)