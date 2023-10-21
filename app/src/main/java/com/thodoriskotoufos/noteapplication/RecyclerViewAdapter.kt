package com.thodoriskotoufos.noteapplication

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class RecyclerViewAdapter(private val dataSet: ArrayList<Note>) :
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    private var onClickListener : OnClickListener? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewTitle: TextView
        val textViewDateTime: TextView
        val textViewContents: TextView

        init {
            // Define click listener for the ViewHolder's View
            textViewTitle = view.findViewById(R.id.itemTitle)
            textViewDateTime = view.findViewById(R.id.itemDatetime)
            textViewContents = view.findViewById(R.id.itemContents)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.items_view, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.textViewTitle.text = dataSet[position].toString()
        viewHolder.textViewDateTime.text = dataSet[position].toString()
        viewHolder.textViewContents.text = dataSet[position].toString()

        viewHolder.itemView.setOnClickListener {
            if (onClickListener != null){
                onClickListener!!.onClick(position)
            }
        }
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int)
    }
    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}
