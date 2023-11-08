package com.example.remember

import android.content.Context
import androidx.room.*

@Database(entities = arrayOf(Alarm::class), version = 1)
@TypeConverters(DayOfWeekConverter::class)
abstract class AlarmDatabase: RoomDatabase() {
    abstract fun alarmDao(): AlarmDao

    companion object {
        private var instance: AlarmDatabase? = null

        @Synchronized
        fun getInstance(context: Context): AlarmDatabase? {
            if (instance == null) {
                synchronized(AlarmDatabase::class){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AlarmDatabase::class.java,
                        "user-database"
                    ).build()
                }
            }
            return instance
        }
    }
}