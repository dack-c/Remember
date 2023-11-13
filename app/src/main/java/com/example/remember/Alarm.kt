package com.example.remember

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Parcelize
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
    val radius:Double,
    val fireOnEscape: Boolean,
    val volume: Double,
    var isActive: Boolean,
    var alreadyFired: Boolean
) : Parcelable