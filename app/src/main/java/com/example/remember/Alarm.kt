package com.example.remember

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity
data class Alarm (
    var name: String,
    var hour: Int,
    var minute: Int,
    val daysOfWeek: List<Int>,
    var longitude: Double,
    var latitude: Double,
    var radius:Double,
    var fireOnEscape: Boolean,
    var volume: Double,
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

    companion object {

        private fun dayToString(day: Int): String {
            val str = when (day) {
                2 -> "월"
                3 -> "화"
                4 -> "수"
                5 -> "목"
                6 -> "금"
                7 -> "토"
                else -> "일"
            }

            return str
        }
        fun daysToString(dayList: List<Int>): String {

            var dayString = ""

            dayList.forEachIndexed() { index, dayInt ->
                val dayStr = Alarm.dayToString(dayInt)
                dayString += dayStr

                if (index < dayList.size - 1) dayString += ", "
            }

            return dayString
        }



    }

}