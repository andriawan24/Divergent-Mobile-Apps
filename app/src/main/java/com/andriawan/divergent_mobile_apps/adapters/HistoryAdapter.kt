package com.andriawan.divergent_mobile_apps.adapters

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.andriawan.divergent_mobile_apps.R
import com.andriawan.divergent_mobile_apps.databinding.ItemHistoryBinding
import com.andriawan.divergent_mobile_apps.models.history_diagnose.Consultation
import com.andriawan.divergent_mobile_apps.utils.BindingAdapter.formatDateTime
import com.andriawan.divergent_mobile_apps.utils.RecyclerDIffUtil

class HistoryAdapter: RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    private var historyList = emptyList<Consultation>()

    class ViewHolder(private val binding: ItemHistoryBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(consultation: Consultation, size: Int) {
            binding.nameAgeTextView.apply {
                text = context.getString(R.string.name_age, consultation.name, consultation.age)
            }

            binding.createdDateTextView.formatDateTime(consultation.created_at)

            if (size <= 1) {
                binding.divider.visibility = GONE
            } else {
                binding.divider.visibility = VISIBLE
            }
        }

        companion object {
            fun from (parent: ViewGroup) : ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemHistoryBinding.inflate(inflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val history = historyList[position]
        holder.bind(history, historyList.size)
    }

    override fun getItemCount(): Int = historyList.size

    fun setData(newList: List<Consultation>) {
        val onboardDiffUtil = RecyclerDIffUtil(historyList, newList)
        val diffUtilResult = DiffUtil.calculateDiff(onboardDiffUtil)
        historyList = newList
        diffUtilResult.dispatchUpdatesTo(this)
    }
}