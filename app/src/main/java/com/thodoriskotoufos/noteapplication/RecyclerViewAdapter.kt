package com.thodoriskotoufos.noteapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewAdapter(
    private val dataSet: ArrayList<Note>,
    private val clickListener: ClickListener
) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewTitle: TextView
        val textViewContents: TextView

        init {
            // Define click listener for the ViewHolder's View
            textViewTitle = view.findViewById(R.id.itemTitle)
            textViewContents = view.findViewById(R.id.itemContents)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.items_view, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewTitle.text = dataSet[position].title
        holder.textViewContents.text = dataSet[position].content

        holder.itemView.setOnClickListener {
            clickListener.onItemClick(dataSet[position])
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    interface ClickListener {
        fun onItemClick(note: Note)
    }

}