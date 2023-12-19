package com.example.remember

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.AlarmManager
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
import android.util.Log
import android.Manifest
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import androidx.annotation.RequiresApi


class MainActivity : AppCompatActivity() {

    private var alarmDataSet = mutableListOf<Alarm>()
    private val alarmRecyclerViewAdapter = AlarmCardRecyclerViewAdapter(alarmDataSet)
    private lateinit var binding: ActivityMainBinding
    private var db: AlarmDatabase? = null

    private val MY_PERMISSIONS_REQ_ACCESS_FINE_LOCATION = 100
    private val MY_PERMISSIONS_REQ_ACCESS_BACKGROUND_LOCATION = 101

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

    override fun onResume() {
        super.onResume()
        loadAlarmCards()
    }

    inner class AlarmUpdateReceiver() : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            binding.noAlarmText.text = "알람을 추가해보세요"
        }

    }

    private fun setupRecyclerView() {
        binding.recyclerView.adapter = alarmRecyclerViewAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.recyclerView.addItemDecoration(VerticalSpaceItemDecoration(32))
    }


    private fun loadAlarmCards() {
        CoroutineScope(Dispatchers.IO).launch { // 다른애 한테 일 시키기
//            val dao = AlarmDatabase.getDao(db!!) ?: return@launch
            val dao = db!!.alarmDao()
            val list = dao.getAll()

            runOnUiThread {
                list.observe(this@MainActivity) { alarmList ->
                    alarmDataSet.clear()
                    alarmList.forEach { alarm ->
                        alarmDataSet.add(alarm)
                    }
                    alarmRecyclerViewAdapter.notifyDataSetChanged()
                } // list.observe End

            } // runOnUiThread End


        } // CoroutineScope End
    }

    private fun addAlarm() {
        val newAlarm = Alarm(
            name = "alamr",
            hour = 1,
            minute = 1,
            daysOfWeek = listOf(1, 2),
            fireOnEscape = true,
            longitude = 1.0,
            latitude = 1.0,
            volume = 1.0,
            isActive = true,
            alreadyFired = true,
            radius = 4.0
        )
//        val newAlarm = Alarm(
//            name = "일어나7777!!",
//            hour = 20,
//            minute = 50,
//            daysOfWeek = listOf(1,2,3,4,5,6,7),
//            fireOnEscape = true,
//            longitude = 128.6092,
//            latitude = 35.8869,
//            volume = 1.0,
//            isActive = true,
//            alreadyFired = false,
//            radius = 500.0
//        )
//
//        Manager.getInstance(this).setAlarm(newAlarm)
//
//        val db = AlarmDatabase.getInstance(applicationContext)
//        CoroutineScope(Dispatchers.IO).launch {
//            db!!.alarmDao().insert(newAlarm)
//        }
        val intent = Intent(this, AlarmSettingActivity::class.java)
        startActivity(intent)

    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        checkPermission()
    }

}