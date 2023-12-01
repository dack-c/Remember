package com.example.remember

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.CompoundButton
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.remember.databinding.ActivityAlarmSettingBinding


class AlarmSettingActivity : AppCompatActivity() {
    private var hour = 0
    private var minute = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAlarmSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "알람 추가하기"

        val timeButton = findViewById<ImageButton>(R.id.timeButton)
        val repectDays = findViewById<LinearLayout>(R.id.repectedDays)
        val showDays = findViewById<LinearLayout>(R.id.showDays)
        val vibrate = findViewById<Switch>(R.id.vibrate)

        //시간 설정
        timeButton.setOnClickListener {
            val timePickerDialog = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { view, selectedHour, selectedMinute ->
                hour = selectedHour
                minute = selectedMinute
                // 시간 설정 처리
            }, hour, minute, false)
            timePickerDialog.show()
        }

        repectDays.setOnClickListener {
            val selectedDays = BooleanArray(7)
            val daysOfWeek = arrayOf("월요일", "화요일", "수요일", "목요일", "금요일", "토요일", "일요일")

            val builder = AlertDialog.Builder(this)
            builder.setTitle("반복 요일")
            builder.setMultiChoiceItems(daysOfWeek, selectedDays) { dialog, which, isChecked ->
                // 선택된 요일 처리
            }
            builder.setPositiveButton("완료") { _, _ ->
                // 완료 버튼 클릭 시 선택된 요일 처리
                val selectedDayText = daysOfWeek.filterIndexed { index, _ -> selectedDays[index] }
                    .map {it.substringBefore("요일")}
                    .joinToString()
                val selectedDaysTextView = TextView(this)
                selectedDaysTextView.text = selectedDayText
                showDays.removeAllViews()
                showDays.addView(selectedDaysTextView)
            }
            builder.show()
        }

        val intent = Intent(this, NaverMapActivity::class.java)
        binding.locationBtn.setOnClickListener{startActivity(intent)}

        vibrate.setOnCheckedChangeListener {buttonView, isChecked ->
            if(isChecked){
                //진동 설정
            } else {}
        }




    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            else -> {}
        }
        return super.onOptionsItemSelected(item)
    }


    inner class MyCheckedChangeListener : CompoundButton.OnCheckedChangeListener{
        override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
            when(buttonView?.id) {
                R.id.leaveToggle ->
                    if(isChecked){
                        //벗어날 때 적용
                    } else{}
                R.id.arriveToggle ->
                    if(isChecked) {
                        // 들어올 때 적용
                    } else {}
            }
        }

    }


}
