package com.example.remember

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.remember.databinding.ActivityAlarmSettingBinding

class AlarmSettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAlarmSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}