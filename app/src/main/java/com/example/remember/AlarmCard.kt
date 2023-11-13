package com.example.remember

data class AlarmCard(
    val id: Int,
    val name: String,
    val locationText: String,
    val timeText: String,
    val isActivated: Boolean
)