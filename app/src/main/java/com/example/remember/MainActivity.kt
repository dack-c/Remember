package com.example.remember

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remember.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var alarmDataSet = mutableListOf<Alarm>()
    private val alarmRecyclerViewAdapter = AlarmCardRecyclerViewAdapter(alarmDataSet)
    private lateinit var binding: ActivityMainBinding
    private var db: AlarmDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AlarmDatabase.getInstance(applicationContext)

        setupRecyclerView()


        binding.addAlarmFab.setOnClickListener {
            addAlarm()
        }


    }

    override fun onResume() {
        super.onResume()
        loadAlarmCards()
    }

    inner class AlarmUpdateReceiver(): BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            binding.noAlarmText.text = "알람을 추가해보세요"
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

            alarmDataSet.clear()
            list.forEach { alarm ->
                alarmDataSet.add(alarm)
            }

            runOnUiThread {
                alarmRecyclerViewAdapter.notifyDataSetChanged()
            }

        }
    }

    private fun addAlarm() {
        val newAlarm = Alarm(
            name = "alamr",
            hour = 1,
            minute = 1,
            daysOfWeek = listOf(1,2),
            fireOnEscape = true,
            longitude = 1.0,
            latitude = 1.0,
            volume = 1.0,
            isActive = true,
            alreadyFired = true,
            radius = 4.0
        )

        val db = AlarmDatabase.getInstance(applicationContext)
        CoroutineScope(Dispatchers.IO).launch {
            db!!.alarmDao().insert(newAlarm)
        }



    }



}