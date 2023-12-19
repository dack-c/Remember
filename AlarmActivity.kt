package com.example.remember

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.remember.databinding.ActivityAlarmBinding

class AlarmActivity : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.alarmName.text = intent.getStringExtra("name")

        // 알람음 객체 생성
        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        mediaPlayer = MediaPlayer.create(this, alarmUri)

        // 음량 설정 및 알람음 실행
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)
        val volumeRatio = intent.getDoubleExtra("volume", 1.0)
        val volume = (maxVolume * volumeRatio).toInt()
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, volume, 0)
        mediaPlayer.start()

        // 알람 종료 버튼
        binding.stopButton.setOnClickListener {
            // 버튼을 클릭하면 MediaPlayer를 정지하고 액티비티를 종료합니다.
            mediaPlayer.stop()
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Activity가 종료될 때 MediaPlayer를 정지하고 리소스를 해제합니다.
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.release()
//        this.cancelAlarm()
    }

    fun cancelAlarm() {
        val alarmCode = intent.getIntExtra("reqCode", 0)
        val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)

        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            PendingIntent.getBroadcast(this,alarmCode,intent,PendingIntent.FLAG_MUTABLE)
        }else{
            PendingIntent.getBroadcast(this,alarmCode,intent,PendingIntent.FLAG_UPDATE_CURRENT)
        }
        Log.d("kkang", "cencel pendingIntent: ${pendingIntent.hashCode()}")

        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }
}