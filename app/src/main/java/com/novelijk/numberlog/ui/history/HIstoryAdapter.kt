package com.novelijk.numberlog.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.novelijk.numberlog.data.DataPoint
import com.novelijk.numberlog.databinding.ItemHistoryBinding

class HistoryAdapter :
    PagingDataAdapter<DataPoint, HistoryAdapter.HistoryViewHolder>(HistoryDiffCallback()) {


    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.onBind(currentItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    inner class HistoryViewHolder(val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        lateinit var currentItem: DataPoint
        fun onBind(dataPoint: DataPoint) {
            currentItem = dataPoint
            binding.apply {
                valueTextView.text = dataPoint.value.toString()
                timeTextView.text = dataPoint.timeFormattedToDisplay
            }
        }

    }

    class HistoryDiffCallback : DiffUtil.ItemCallback<DataPoint>() {
        override fun areItemsTheSame(oldItem: DataPoint, newItem: DataPoint): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DataPoint, newItem: DataPoint): Boolean {
            return oldItem == newItem
        }

    }

}