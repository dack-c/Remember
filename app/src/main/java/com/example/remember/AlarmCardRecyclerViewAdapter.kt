package com.example.remember

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.materialswitch.MaterialSwitch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AlarmCardRecyclerViewAdapter(private val dataSet: MutableList<Alarm>) :
    RecyclerView.Adapter<AlarmCardRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView
        val timeTextView: TextView
        val locationTextView: TextView
        val switch: MaterialSwitch
        val container: LinearLayout
        val context: Context

        init {
            // Define click listener for the ViewHolder's View.
            context = view.context
            nameTextView = view.findViewById(R.id.alarm_card_title)
            timeTextView = view.findViewById(R.id.alarm_card_time_text)
            locationTextView = view.findViewById(R.id.alarm_card_location_text)
            switch = view.findViewById(R.id.alarm_card_switch)
            container = view.findViewById(R.id.alarm_card_container)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.alarm_card_view, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {


        val alarm = dataSet[position]

        viewHolder.nameTextView.text = alarm.name
        viewHolder.timeTextView.text = alarm.timeText
        //viewHolder.locationTextView.text = "${alarm.latitude}"
        var shortenedAddress = alarm.koreanAddress.replace("대한민국 ", "")
        shortenedAddress = shortenedAddress.replace("광역시", "")
        viewHolder.locationTextView.text = shortenedAddress
        viewHolder.switch.isChecked = alarm.isActive

        viewHolder.container.setOnClickListener {
            val intent = Intent(viewHolder.context, AlarmSettingActivity::class.java)
            intent.putExtra("alarm", alarm)
            intent.putExtra("alarmId", alarm.id)
            intent.putExtra("isEdit", true)
            viewHolder.context.startActivity(intent)
        }

        viewHolder.switch.setOnCheckedChangeListener { button, isChecked ->

            CoroutineScope(Dispatchers.IO).launch {
                val db = AlarmDatabase.getInstance(viewHolder.context)
                db!!.alarmDao().delete(alarm)
            }

        }
    }

    override fun getItemCount() = dataSet.size



}
