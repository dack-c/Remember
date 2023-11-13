package com.example.remember

data class Time (
    val hour: Int,
    val minute: Int,
) {
    fun getTimeString(): String {
        return "$hour:$minute"
    }
}