package com.example.remember

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.remember.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //test용: naver map api 잘 작동하는지 확인
        val intent = Intent(this, NaverMapActivity::class.java)
        binding.button.setOnClickListener{startActivity(intent)}
    }
}