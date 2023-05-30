package com.example.vocabvault

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class TagsAdapter(
    private val dataList: MutableList<String>, private val tagClickListener: OnTagClickListener
) : RecyclerView.Adapter<TagsAdapter.ViewHolder>() {
    private var selectedPosition: Int = RecyclerView.NO_POSITION
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tags_list_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tag = dataList[position]
        holder.bind(tag, position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    // Method to get the selected item based on position
    fun getSelectedItem(position: Int): String? {
        if (selectedPosition == position && position != RecyclerView.NO_POSITION) {
            return dataList[selectedPosition]
        }
        return null
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tagTextView: TextView = itemView.findViewById(R.id.tag_text_view_id)

        // Method to get the selected item based on position
        fun bind(tag: String, position: Int) {
            tagTextView.text = tag
            // Set the background color based on the selected position
            if (position == selectedPosition) {
                tagTextView.setBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context, R.color.primary_variant
                    )
                )
            } else {
                tagTextView.setBackgroundColor(Color.TRANSPARENT)
            }

            // Set the click listener for the TextView
            tagTextView.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = position
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)
                tagClickListener.onTagClick(position)
            }
        }
    }
}
