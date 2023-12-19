package com.example.myapp

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.CompoundButton
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.ToggleButton
import androidx.annotation.RequiresApi
import com.example.myapp.databinding.ActivityAlarmSettingBinding
import androidx.appcompat.widget.SwitchCompat
import org.w3c.dom.Text

class AlarmSettingActivity : AppCompatActivity() {
    private var hour = 0
    private var minute = 0
    private lateinit var binding: ActivityAlarmSettingBinding

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
                false
            )

            timePickerDialog.setTitle("시간 설정")
            timePickerDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            timePickerDialog.show()
        }



        // 요일 설정
        repectDays.setOnClickListener {
            val selectedDays = BooleanArray(7)
            val daysOfWeek = arrayOf("월요일", "화요일", "수요일", "목요일", "금요일", "토요일", "일요일")

            val builder = AlertDialog.Builder(this)
            builder.setTitle("반복 요일")
            builder.setMultiChoiceItems(daysOfWeek, selectedDays) { _, which, isChecked ->
                // 선택된 요일 처리
            }
            builder.setPositiveButton("완료") { _, _ ->
                // 완료 버튼 클릭 시 선택된 요일 처리
                val selectedDayText =
                    daysOfWeek.filterIndexed { index, _ -> selectedDays[index] }
                        .map { it.substringBefore("요일") }
                        .joinToString()
                val selectedDaysTextView = TextView(this)
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
        binding.locationBtn.setOnClickListener { startActivity(intent) }

        // leaveToggle 버튼을 찾아옵니다.
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

    private fun saveAlarm() {
        // "저장하기" 버튼을 눌렀을 때 수행할 동작 구현
    }
}
