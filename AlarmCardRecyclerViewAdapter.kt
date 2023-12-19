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

//private const val TYPE_HEADER = 0
//private const val TYPE_ITEM = 1
//private const val TYPE_FOOTER = 2

//internal class HeaderViewHolder(headerView: View?) : RecyclerView.ViewHolder(
//    headerView!!
//)

class AlarmCardRecyclerViewAdapter(private val dataSet: MutableList<Alarm>) :
    RecyclerView.Adapter<AlarmCardRecyclerViewAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
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
        // Create a new view, which defines the UI of the list item

        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.alarm_card_view, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        var alarm = dataSet[position]

        viewHolder.nameTextView.text = alarm.name
        viewHolder.timeTextView.text = alarm.timeText
        viewHolder.locationTextView.text = "${alarm.latitude}"
        viewHolder.switch.isChecked = alarm.isActive

        viewHolder.container.setOnClickListener {
            val intent = Intent(viewHolder.context, AlarmSettingActivity::class.java)
            intent.putExtra("alarm", alarm)
            viewHolder.context.startActivity(intent)
        }

        viewHolder.switch.setOnCheckedChangeListener { button, isChecked ->
            val updatedAlarm = alarm.copy(isActive = isChecked)
            updatedAlarm.id = alarm.id

            CoroutineScope(Dispatchers.IO).launch {
                val db = AlarmDatabase.getInstance(viewHolder.context)
                val updatedRows = db!!.alarmDao().update(updatedAlarm)
                println(updatedRows)
            }

        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size



}
