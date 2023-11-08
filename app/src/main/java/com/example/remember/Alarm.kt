package com.example.remember

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Alarm (
    @PrimaryKey
    val id: Int,
    val name: String,
    val hour: Int,
    val minute: Int,
    val daysOfWeek: List<Int>,
    val longitude: Double,
    val latitude: Double,
    val fireOnEscape: Boolean,
    val volume: Double
)