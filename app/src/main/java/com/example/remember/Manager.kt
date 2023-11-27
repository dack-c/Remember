package com.example.remember

import android.annotation.SuppressLint
import java.util.*
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import android.widget.Toast
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import java.util.concurrent.TimeUnit

class Manager {
    //멤버함수 호출방법: Manager.getInstance(this).멤버함수이름(매개변수)
    private lateinit var alarmModels: MutableList<Alarm>
    lateinit var mGeofencingClient: GeofencingClient
    lateinit var mGeofenceList: ArrayList<Geofence>
    private lateinit var mGeofencePendingIntent: PendingIntent
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

    fun doUpdateGpsWorkWithPeriodic() { //백그라운드 위치 업데이트 시작
        Log.d("CheckGpsWorker", "worker 시작함수 진입")
        val workRequest = PeriodicWorkRequestBuilder<UpdateGpsWorker>(15, TimeUnit.MINUTES).build() //15문마다 위치 업데이트
        WorkManager.getInstance(context).enqueueUniquePeriodicWork("checkGps", ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, workRequest)
    }

    fun changeActivationState(alarmId: Int, isActive: Boolean) { //초기화면에서 알람 비활성화 버튼 클릭 시
        for(alarmModel in alarmModels) {
            if(alarmModel.id == alarmId) {
                alarmModel.isActive = isActive
                return
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun setAlarm(alarmModel: Alarm) {
        if(!(::alarmModels.isInitialized)) {
            alarmModels = mutableListOf<Alarm>()
        }
        alarmModels.add(alarmModel)
        if(!(::mGeofenceList.isInitialized)) {
            mGeofenceList = ArrayList()
        }
        if(!(::mGeofencingClient.isInitialized)) {
            mGeofencingClient = LocationServices.getGeofencingClient(context)
        }

        val transitionType = when(alarmModel.fireOnEscape) {
            true -> Geofence.GEOFENCE_TRANSITION_ENTER
            false -> Geofence.GEOFENCE_TRANSITION_EXIT
        }
        mGeofenceList.add(Geofence.Builder()
            .setRequestId(alarmModel.id.toString())
            .setCircularRegion(
                alarmModel.latitude,
                alarmModel.longitude,
                alarmModel.radius.toFloat()
            )
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(transitionType)
            .build())

        mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent(alarmModel))?.run {
            addOnSuccessListener {
                Toast.makeText(context, "add Success", Toast.LENGTH_LONG).show()
            }
            addOnFailureListener { e ->
                Toast.makeText(context, "add Fail", Toast.LENGTH_LONG).show()
                Log.e("kkang", e.localizedMessage ?: "Unknown error")
            }
        }
    }

    private fun getGeofencingRequest(): GeofencingRequest {
        val builder = GeofencingRequest.Builder()
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
        builder.addGeofences(mGeofenceList)
        return builder.build()
    }

    private fun getGeofencePendingIntent(alarmModel: Alarm): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("AlarmModel", alarmModel)
        mGeofencePendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            PendingIntent.getBroadcast(context, alarmModel.id, intent, PendingIntent.FLAG_MUTABLE)
        }else{
            PendingIntent.getBroadcast(context, alarmModel.id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        return mGeofencePendingIntent
    }
}