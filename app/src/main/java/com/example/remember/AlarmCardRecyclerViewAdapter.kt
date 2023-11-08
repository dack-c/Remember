package com.example.remember

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.materialswitch.MaterialSwitch

//private const val TYPE_HEADER = 0
//private const val TYPE_ITEM = 1
//private const val TYPE_FOOTER = 2

//internal class HeaderViewHolder(headerView: View?) : RecyclerView.ViewHolder(
//    headerView!!
//)

class AlarmCardRecyclerViewAdapter(private val dataSet: Array<AlarmCard>) :
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

        init {
            // Define click listener for the ViewHolder's View.
            nameTextView = view.findViewById(R.id.alarm_card_title)
            timeTextView = view.findViewById(R.id.alarm_card_time_text)
            locationTextView = view.findViewById(R.id.alarm_card_location_text)
            switch = view.findViewById(R.id.alarm_card_switch)
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
        viewHolder.nameTextView.text = dataSet[position].name
        viewHolder.timeTextView.text = dataSet[position].timeText
        viewHolder.locationTextView.text = dataSet[position].locationText

        viewHolder.switch.isChecked = dataSet[position].isActivated
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size


}
