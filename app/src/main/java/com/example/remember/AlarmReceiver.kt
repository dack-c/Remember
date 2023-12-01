package com.example.remember

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import java.util.Calendar
import kotlin.math.abs

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alarmModel: Alarm? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("AlarmModel", Alarm::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("AlarmModel") as? Alarm
        }

        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent != null && geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes
                .getStatusCodeString(geofencingEvent.errorCode)
            Log.e("kkang", errorMessage)
            return
        }

        val geofenceTransition = geofencingEvent?.geofenceTransition

        val transitionStr = when(geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> "enter"
            Geofence.GEOFENCE_TRANSITION_EXIT -> "exit"
            else -> "other"
        }
        Log.d("kkang", "transition type: $transitionStr")

        if (alarmModel != null && alarmModel.isActive && (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER
                    || geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT)) {
            for (dayOfWeek in alarmModel.daysOfWeek) {
                val curCalendar = Calendar.getInstance()

                if(dayOfWeek == curCalendar.get(Calendar.DAY_OF_WEEK)) { // 두 Calendar 인스턴스의 차이가 1시간 이내인지 확인
                    val curTime = curCalendar.get(Calendar.HOUR_OF_DAY)*60 + curCalendar.get(Calendar.MINUTE)
                    val setTime = alarmModel.hour*60 + alarmModel.minute
                    val diff = abs(curTime-setTime)
                    if(diff <= 60) {
                        val activityIntent = Intent(context, AlarmActivity::class.java).apply {
                            putExtra("name", alarmModel.name)
                            putExtra("volume", alarmModel.volume)
                            putExtra("reqCode", alarmModel.id)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(activityIntent) //알람 실행
                    }
                }
            }
        }
    }
}