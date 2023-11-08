package com.example.remember

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.remember.databinding.ActivityMainBinding
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val alarmCardDataSet = arrayOf<AlarmCard>(
            AlarmCard(1, "first alarm", "대구", "6:00",true),
            AlarmCard(2, "2 alarm", "대구", "6:00",false),
            AlarmCard(3, "3 alarm", "대구", "6:00",true),
            AlarmCard(4, "4 alarm", "대구", "6:00",true),
        )

        val customAdapter = AlarmCardRecyclerViewAdapter(alarmCardDataSet)

        binding.recyclerView.adapter = customAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this,RecyclerView.VERTICAL, false)
        binding.recyclerView.addItemDecoration(VerticalSpaceItemDecoration(32))


        val db = AlarmDatabase.getInstance(applicationContext)
        CoroutineScope(Dispatchers.IO).launch { // 다른애 한테 일 시키기
            val list = db!!.alarmDao().getAll()
            println("here!!")
            println(list)
        }

        binding.addAlarmFab.setOnClickListener {
            val newAlarm = Alarm(
                id = 2,
                name = "alamr",
                hour = 1,
                minute = 1,
//                daysOfWeek = arrayOf(1,2),
                fireOnEscape = true,
                longitude = 1.0,
                latitude = 1.0,
                volume = 1.0
            )

            val db = AlarmDatabase.getInstance(applicationContext)
            CoroutineScope(Dispatchers.IO).launch {
                db!!.alarmDao().insert(newAlarm)
            }
        }


    }
}