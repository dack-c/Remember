package com.example.remember

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import android.widget.ToggleButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.example.remember.databinding.ActivityAlarmSettingBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AlarmSettingActivity : AppCompatActivity() {
    private var hour = 0
    private var minute = 0
    private lateinit var binding: ActivityAlarmSettingBinding

    private var latitude = 1.0
    private var longitude = 1.0
    private var koreanAddress = "temp"
    private var radius = 100.0
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "알람 추가하기"

        val timeButton = findViewById<ImageButton>(R.id.timeButton)
        val repectDays = findViewById<LinearLayout>(R.id.repectedDays)
        val showDays = findViewById<LinearLayout>(R.id.showDays)
        val select_Days = findViewById<TextView>(R.id.select_Days)
        val vibrate = findViewById<SwitchCompat>(R.id.vibrate)

        // 시간 설정
        timeButton.setOnClickListener {
            val timePickerDialog = TimePickerDialog(
                this@AlarmSettingActivity,
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                    hour = selectedHour
                    minute = selectedMinute

                    // 시간 설정 처리
                    val selectedTimeText = String.format("\t%02d:%02d", selectedHour, selectedMinute)
                    updateTimeTextView(selectedTimeText)
                },
                hour,
                minute,
                true
            )

            timePickerDialog.setTitle("시간 설정")
            timePickerDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            timePickerDialog.show()
        }



        // 요일 설정
        repectDays.setOnClickListener {
            val selectedDays = BooleanArray(7)
            val daysOfWeek = arrayOf("월요일", "화요일", "수요일", "목요일", "금요일", "토요일", "일요일")

            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("반복 요일")
            builder.setMultiChoiceItems(daysOfWeek, selectedDays) { dialog, which, isChecked ->
                // 선택된 요일 처리
            }
            builder.setPositiveButton("완료") { _, _ ->
                // 완료 버튼 클릭 시 선택된 요일 처리
                val selectedDayText = daysOfWeek.filterIndexed { index, _ -> selectedDays[index] }
                    .map { it.substringBefore("요일") }
                    .joinToString()
                val selectedDaysTextView = select_Days
                selectedDaysTextView.text = selectedDayText
                showDays.removeAllViews()
                showDays.addView(selectedDaysTextView)
            }
            builder.show()

        }


        // 음량 & 진동 조절하기

        // 음량 조절하기
        val seekBar: SeekBar = findViewById(R.id.volumeSeekbar)
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // 음량 변경 처리
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                // 터치 시작 처리
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                // 터치 종료 처리
            }
        })

        // 진동 설정
        vibrate.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // 진동 설정 처리
            } else {
                // 진동 해제 처리
            }
        }

        // 위치 설정
        val intent = Intent(this, NaverMapActivity::class.java)
        binding.locationBtn.setOnClickListener { startActivityForResult(intent, 1) }

        val leaveToggle = findViewById<ToggleButton>(R.id.leaveToggle)
        val arriveToggle = findViewById<ToggleButton>(R.id.arriveToggle)

        leaveToggle.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                // 버튼이 체크된 상태일 때의 처리
                leaveToggle.setBackgroundResource(R.drawable.button_backgroud_checked)
            } else {
                // 버튼이 체크되지 않은 상태일 때의 처리
                leaveToggle.setBackgroundResource(R.drawable.button_background_unchecked)
            }
        }
        arriveToggle.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                // 버튼이 체크된 상태일 때의 처리
                arriveToggle.setBackgroundResource(R.drawable.button_backgroud_checked)
            } else {
                // 버튼이 체크되지 않은 상태일 때의 처리
                arriveToggle.setBackgroundResource(R.drawable.button_background_unchecked)
            }
        }



    }

    private fun updateTimeTextView(time: String) {
        val timeTextView = findViewById<TextView>(R.id.timeTextView)
        timeTextView.text = time
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_save, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> {
                // "저장하기" 버튼을 눌렀을 때 수행할 동작 추가
                // 예: saveAlarm() 함수 호출
                saveAlarm()
                return true
            }

            android.R.id.home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // 현재 액티비티 종료
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1 && resultCode == Activity.RESULT_OK) {
            latitude = data?.getDoubleExtra("latitude", 1.0)!!
            longitude = data?.getDoubleExtra("longitude", 1.0)!!
            koreanAddress = data?.getStringExtra("koreanAddress")!!
            radius = data?.getDoubleExtra("radius", 100.0)!!
        }
    }

    private fun saveAlarm() {
        // 이름 가져오기
        val nameEditText = findViewById<EditText>(R.id.name)
        val name = nameEditText.text.toString()

        // 시간 가져오기
        val timeTextView = findViewById<TextView>(R.id.timeTextView)
        val selectedTimeText = timeTextView.text.toString()

        // 선택된 요일 가져오기
        val selectedDaysTextView = findViewById<TextView>(R.id.select_Days)
        Log.d("test: ", "$selectedDaysTextView.text")
        val selectedDayText = selectedDaysTextView.text.toString()
        val selectedDaysList = selectedDayText
            .split(",") // 쉼표로 분할
            .map {
                val trimDay = it.trim()
                val dayInt = when (trimDay) {
                    "월" -> 2
                    "화" -> 3
                    "수" -> 4
                    "목" -> 5
                    "금" -> 6
                    "토" -> 7
                    else -> 1
                }
                dayInt
            } // 각 부분을 정수로 변환

        // 음량 값 가져오기
        val volumeSeekBar = findViewById<SeekBar>(R.id.volumeSeekbar)
        val volume = volumeSeekBar.progress.toDouble() / 100.0

        // 진동 설정 값 가져오기
        val vibrateSwitch = findViewById<SwitchCompat>(R.id.vibrate)
        val isVibrateEnabled = vibrateSwitch.isChecked

        val leaveToggle = findViewById<ToggleButton>(R.id.leaveToggle)
        val isLeave = leaveToggle.isChecked

        val arriveToggle = findViewById<ToggleButton>(R.id.arriveToggle)
        val isarrive = arriveToggle.isChecked

        // 알람 객체 생성
        val newAlarm = Alarm(
            name = name,
            hour = hour,
            minute = minute,
            daysOfWeek = selectedDaysList,
            longitude = longitude,
            latitude = latitude,
            radius = radius,
            fireOnEscape = isarrive,
            volume = volume,
            isActive = true,
            alreadyFired = false,
            koreanAddress = koreanAddress
        )
        Manager.getInstance(this).setAlarm(newAlarm)

        val db = AlarmDatabase.getInstance(applicationContext)
        CoroutineScope(Dispatchers.IO).launch {
            db!!.alarmDao().insert(newAlarm)
        }
//        val intent = Intent(this, AlarmSettingActivity::class.java)
//        startActivity(intent)
        finish()

    }

}