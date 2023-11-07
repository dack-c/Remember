package com.example.remember

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

//private const val TYPE_HEADER = 0
//private const val TYPE_ITEM = 1
//private const val TYPE_FOOTER = 2

//internal class HeaderViewHolder(headerView: View?) : RecyclerView.ViewHolder(
//    headerView!!
//)

class AlarmCardRecyclerViewAdapter(private val dataSet: Array<String>) :
    RecyclerView.Adapter<AlarmCardRecyclerViewAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView

        init {
            // Define click listener for the ViewHolder's View.
            textView = view.findViewById(R.id.alarm_card_title)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item

//        if (viewType == TYPE_HEADER) {
//            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header, parent, false);
//            holder = new HeaderViewHolder(view);

        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.alarm_card_view, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.textView.text = dataSet[position]
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

//    override fun getItemViewType(position: Int): Int {
//        return if (position == 0)
//            TYPE_HEADER;
//        else
//            TYPE_ITEM;
////        return super.getItemViewType(position)
//    }

}
