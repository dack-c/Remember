package com.example.remember

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AlarmDao {
    @Insert
    fun insert(alarm: Alarm)

    @Update
    fun update(alarm: Alarm): Int

    @Delete
    fun delete(alarm: Alarm)

    @Query("SELECT * FROM Alarm")
    fun getAll(): LiveData<List<Alarm>>

}