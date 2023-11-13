package com.example.remember

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // 알람이 실행될 때의 동작을 여기에 작성합니다.
        val name = intent.getStringExtra("name")
        val volume = intent.getDoubleExtra("volume", 1.0)
        val reqCode = intent.getIntExtra("id", 0)
        val activityIntent = Intent(context, AlarmActivity::class.java).apply {
            putExtra("name", name)
            putExtra("volume", volume)
            putExtra("reqCode", reqCode)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(activityIntent)
    }
}