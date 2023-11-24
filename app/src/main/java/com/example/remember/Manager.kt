package com.example.remember

import android.annotation.SuppressLint
import android.location.Location
import java.util.*
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import kotlin.math.abs

class Manager {
    //멤버함수 호출방법: Manager.getInstance(this).멤버함수이름(매개변수)
//    private val geofencingClient: GeofencingClient = LocationServices.getGeofencingClient(context)
    private lateinit var locationManager: LocationManager
    private lateinit var alarmModels: MutableList<Alarm>
    private lateinit var pendingIntent: PendingIntent
    private lateinit var alarmManager: AlarmManager
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

    fun changeActivationState(alarmId: Int, isActive: Boolean) { //초기화면에서 알람 비활성화 버튼 클릭 시
        for(alarmModel in alarmModels) {
            if(alarmModel.id == alarmId) {
                alarmModel.isActive = isActive
                return
            }
        }
    }

    fun setAlarm(alarmModel: Alarm) {
        if(!(::alarmModels.isInitialized)) {
            alarmModels = mutableListOf<Alarm>();
        }
        alarmModels.add(alarmModel)
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        try {
            // 위치 업데이트 요청
            locationManager.requestLocationUpdates( //함수 호출할때마다 따로따로 동작가능
                LocationManager.GPS_PROVIDER, 5000L, 10f,
                this.createLocationListener(alarmModel))
        } catch(ex: SecurityException) {
            Log.d("myTag", "Security Exception, no location available")
        }
    }

    private fun createLocationListener(alarmModel: Alarm): LocationListener {
        return object : LocationListener {
            override fun onLocationChanged(location: Location) { //주기마다 실행됨
                val isLocationConditionSatisfied = when(alarmModel.fireOnEscape) {
                    true -> isAtLocation(location, alarmModel)
                    false -> !isAtLocation(location, alarmModel)
                }
                if (alarmModel.isActive && !(alarmModel.alreadyFired) && isLocationConditionSatisfied && isInTimeRange(alarmModel)) { // 알람 조건에 도달 했으면
                    startAlarm(alarmModel) //알람 실행
                    alarmModel.alreadyFired = true //위치 조건 다시 불만족 될 때까지 알람 비활성화
                }
                else if (!isLocationConditionSatisfied && alarmModel.alreadyFired) { //위치 조건 다시 만족->불만족 되면
                    alarmModel.alreadyFired = false //다시 만족할 때 알람 울리도록 설정
                }
            }
            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }
    }

    // 특정 위치 반경 안으로 들어왔는지 확인하는 메소드
    private fun isAtLocation(location: Location, alarmModel: Alarm): Boolean {
        // 사용자의 위치와 특정 위치 사이의 거리 계산
        val distance = FloatArray(1)
        Location.distanceBetween(location.latitude, location.longitude, alarmModel.latitude, alarmModel.longitude, distance)

        // 거리가 radius미터 이내인지 확인
        return distance[0] <= alarmModel.radius
    }

    // 현재 시간이 설정한 시간과 1시간 차이인지 확인하는 메소드
    private fun isInTimeRange(alarmModel: Alarm): Boolean {
        for (dayOfWeek in alarmModel.daysOfWeek) {
            val curCalendar = Calendar.getInstance()

            if(dayOfWeek == curCalendar.get(Calendar.DAY_OF_WEEK)) { // 두 Calendar 인스턴스의 차이가 1시간 이내인지 확인
                val curTime = curCalendar.get(Calendar.HOUR_OF_DAY)*60 + curCalendar.get(Calendar.MINUTE)
                val setTime = alarmModel.hour*60 + alarmModel.minute
                val diff = abs(curTime-setTime)
                if(diff <= 60) {
                    return true
                }
            }
        }
        return false
    }

    // 알람을 실행하는 메소드
    @SuppressLint("ScheduleExactAlarm")
    private fun startAlarm(alarmModel: Alarm) {
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("name", alarmModel.name)
        intent.putExtra("volume", alarmModel.volume)
        intent.putExtra("id", alarmModel.id)
        pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            PendingIntent.getBroadcast(context, alarmModel.id, intent, PendingIntent.FLAG_MUTABLE)
        }else{
            PendingIntent.getBroadcast(context, alarmModel.id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        Log.d("kkang", "start pendingIntent: ${pendingIntent.hashCode()}")

        // 알람 실행
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent)
    }
}