package com.example.remember

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.remember.databinding.ActivityNaverMapBinding

class NaverMapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityNaverMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // first commit
    }
}