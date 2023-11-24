package com.example.remember

import android.app.AlarmManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remember.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.provider.Settings


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


        binding.addAlarmFab.setOnClickListener {
            addAlarm()
            //테스트 용
//            val intent = Intent(this, AlarmSettingActivity::class.java)
//            startActivity(intent)
        }


    }

    override fun onStart() {
        super.onStart()

        loadAlarmCards()
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


            runOnUiThread {
                alarmRecyclerViewAdapter.notifyDataSetChanged()
            }

        }
    }

    private fun addAlarm() {
        val newAlarm = Alarm(
            name = "일어나7777!!",
            hour = 0,
            minute = 50,
                daysOfWeek = listOf(1,2,5,6),
            fireOnEscape = true,
            longitude = 128.6092,
            latitude = 35.8869,
            volume = 1.0,
            isActive = true,
            alreadyFired = false,
            radius = 5.0
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { //알람 권한 허용 됬는지 확인
            val alarmManager = ContextCompat.getSystemService(this, AlarmManager::class.java)
            if (alarmManager?.canScheduleExactAlarms() == false) {
                Intent().also { intent ->
                    intent.action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                    this.startActivity(intent)
                }
            }
        }

        Manager.getInstance(this).setAlarm(newAlarm)

        val db = AlarmDatabase.getInstance(applicationContext)
        CoroutineScope(Dispatchers.IO).launch {
            db!!.alarmDao().insert(newAlarm)
        }
    }



}