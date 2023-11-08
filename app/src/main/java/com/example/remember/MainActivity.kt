package com.example.remember

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.remember.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import okhttp3.internal.toImmutableList
import kotlin.reflect.typeOf

class MainActivity : AppCompatActivity() {

    private var alarmCardDataSet = mutableListOf<AlarmCard>()
    private val alarmRecyclerViewAdapter = AlarmCardRecyclerViewAdapter(alarmCardDataSet)
    private lateinit var binding: ActivityMainBinding
    private var db: AlarmDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AlarmDatabase.getInstance(applicationContext)

        setupRecyclerView()
        loadAlarmCards()

        binding.addAlarmFab.setOnClickListener {
            addAlarm()
        }


    }

    private fun setupRecyclerView() {
        binding.recyclerView.adapter = alarmRecyclerViewAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this,RecyclerView.VERTICAL, false)
        binding.recyclerView.addItemDecoration(VerticalSpaceItemDecoration(32))
    }

    private fun loadAlarmCards() {
        CoroutineScope(Dispatchers.IO).launch { // 다른애 한테 일 시키기
            val dao = db?.alarmDao() ?: return@launch
            val list = dao.getAll()

            alarmCardDataSet.clear()
            list.forEach { alarm ->
                val newAlarmCard = AlarmCard(
                    id = alarm.id,
                    name = alarm.name,
                    locationText = "${alarm.latitude}, ${alarm.longitude}",
                    timeText = "${alarm.hour}:${alarm.minute}",
                    isActivated = true,
                )
                alarmCardDataSet.add(newAlarmCard)
            }

            alarmRecyclerViewAdapter.notifyDataSetChanged()
        }
    }

    private fun addAlarm() {
        val newAlarm = Alarm(
            id = 3,
            name = "alamr",
            hour = 1,
            minute = 1,
                daysOfWeek = listOf(1,2),
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