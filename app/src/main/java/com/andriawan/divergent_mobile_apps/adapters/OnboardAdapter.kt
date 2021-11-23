package com.andriawan.divergent_mobile_apps.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.andriawan.divergent_mobile_apps.databinding.ItemOnboardingBinding
import com.andriawan.divergent_mobile_apps.models.OnboardModel
import com.andriawan.divergent_mobile_apps.utils.RecyclerDIffUtil

class OnboardAdapter: RecyclerView.Adapter<OnboardAdapter.ViewHolder>() {

    var listOnBoard = emptyList<OnboardModel>()

    class ViewHolder(private val binding: ItemOnboardingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(onboard: OnboardModel) {
            binding.titleTextView.text = binding.root.context.getString(onboard.title)
            binding.slideImageView.setImageResource(onboard.imageRes)
            binding.descriptionTextView.text = binding.root.context.getString(onboard.subtitle)
        }

        companion object {
            fun from (parent: ViewGroup) : ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemOnboardingBinding.inflate(inflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listOnBoard[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int = listOnBoard.size

    fun setData(newList: List<OnboardModel>) {
        val onboardDiffUtil = RecyclerDIffUtil(listOnBoard, newList)
        val diffUtilResult = DiffUtil.calculateDiff(onboardDiffUtil)
        listOnBoard = newList
        diffUtilResult.dispatchUpdatesTo(this)
    }
}