package com.example.remember

import androidx.room.*

@Dao
interface AlarmDao {
    @Insert
    fun insert(alarm: Alarm)

    @Update
    fun update(alarm: Alarm)

    @Delete
    fun delete(alarm: Alarm)

    @Query("SELECT * FROM Alarm")
    fun getAll(): List<Alarm>
}