package com.example.remember

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remember.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dataset = arrayOf("January", "February", "March", "asdf", "asdfasdf", "asdfsadfadf", "asdf asd")
        val customAdapter = AlarmCardRecyclerViewAdapter(dataset)

        binding.recyclerView.adapter = customAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this,RecyclerView.VERTICAL, false)
        binding.recyclerView.addItemDecoration(VerticalSpaceItemDecoration(32))


    }
}