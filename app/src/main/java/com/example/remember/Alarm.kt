package com.example.remember

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity
data class Alarm (
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
    var alreadyFired: Boolean,
    var koreanAddress: String
) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    val timeText: String
        get() {
            return String.format("%02d: %02d", hour, minute)
        }
}