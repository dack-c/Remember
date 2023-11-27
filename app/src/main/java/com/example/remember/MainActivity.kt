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
import android.util.Log
import android.Manifest
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import androidx.annotation.RequiresApi


class MainActivity : AppCompatActivity() {

    private var alarmCardDataSet = mutableListOf<AlarmCard>()
    private val alarmRecyclerViewAdapter = AlarmCardRecyclerViewAdapter(alarmCardDataSet)
    private lateinit var binding: ActivityMainBinding
    private var db: AlarmDatabase? = null

    private val MY_PERMISSIONS_REQ_ACCESS_FINE_LOCATION = 100
    private val MY_PERMISSIONS_REQ_ACCESS_BACKGROUND_LOCATION = 101

//    private var mService: LocationUpdatesService? = null
//    private var mBound = false
//
//    private var mServiceConnection = object : ServiceConnection {
//        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
//            val binder = service as LocationUpdatesService.LocalBinder
//            mService = binder.getService()
//            mBound = true
//        }
//
//        override fun onServiceDisconnected(name: ComponentName?) {
//            mService = null
//            mBound = false
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AlarmDatabase.getInstance(applicationContext)

        setupRecyclerView()

        checkPermission()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { //알람 권한 허용 됬는지 확인
            val alarmManager = ContextCompat.getSystemService(this, AlarmManager::class.java)
            if (alarmManager?.canScheduleExactAlarms() == false) {
                Intent().also { intent ->
                    intent.action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                    this.startActivity(intent)
                }
            }
        }


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
            hour = 18,
            minute = 50,
                daysOfWeek = listOf(1,2,3,4,5,6,7),
            fireOnEscape = true,
            longitude = 128.6092,
            latitude = 35.8869,
            volume = 1.0,
            isActive = true,
            alreadyFired = false,
            radius = 500.0
        )

        Manager.getInstance(this).setAlarm(newAlarm)

        val db = AlarmDatabase.getInstance(applicationContext)
        CoroutineScope(Dispatchers.IO).launch {
            db!!.alarmDao().insert(newAlarm)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        checkPermission()
    }


    private fun checkPermission() {
        val permissionAccessFineLocationApproved = ActivityCompat
            .checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
        if (permissionAccessFineLocationApproved) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val backgroundLocationPermissionApproved = ActivityCompat
                    .checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED

                if (!backgroundLocationPermissionApproved) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                        MY_PERMISSIONS_REQ_ACCESS_BACKGROUND_LOCATION
                    )
                } else {
                    Manager.getInstance(this).doUpdateGpsWorkWithPeriodic()//백그라운드 위치 업데이트 시작
                }
            } else {
                Manager.getInstance(this).doUpdateGpsWorkWithPeriodic()//백그라운드 위치 업데이트 시작
            }
        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQ_ACCESS_FINE_LOCATION
            )
        }
    }

//    private fun bindAndStartService() {
//        bindService(Intent(this, LocationUpdatesService::class.java), mServiceConnection,
//            BIND_AUTO_CREATE)
//        mService!!.requestLocationUpdates()
//    }

}