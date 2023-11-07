package com.example.remember

import android.content.Context

class Manager {
    //멤버함수 호출방법: Manager.getInstance(this).멤버함수이름(매개변수)
    companion object {
        private var instance: Manager? = null

        private lateinit var context: Context

        fun getInstance(_context: Context): Manager {
            return instance ?: synchronized(this) {
                instance ?: Manager().also {
                    context = _context
                    instance = it
                }
            }
        }
    }

    fun setAlarm(alarmModel: AlarmModel) {

    }

    fun changeActivationState(alarmId: Int, isActive: Boolean) {

    }

}