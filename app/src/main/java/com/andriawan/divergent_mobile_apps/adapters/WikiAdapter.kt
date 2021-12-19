package com.andriawan.divergent_mobile_apps.adapters

import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.andriawan.divergent_mobile_apps.R
import com.andriawan.divergent_mobile_apps.databinding.ItemWikiBinding
import com.andriawan.divergent_mobile_apps.models.symptoms.response.Symptom
import com.andriawan.divergent_mobile_apps.ui.wiki.WikiViewModel
import com.andriawan.divergent_mobile_apps.utils.RecyclerDIffUtil

class WikiAdapter(val viewModel: WikiViewModel) : RecyclerView.Adapter<WikiAdapter.ViewHolder>() {

    private var wikiList = emptyList<Symptom>()

    class ViewHolder(private val binding: ItemWikiBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(symptom: Symptom, viewModel: WikiViewModel) {
            binding.symptom = symptom
            binding.listener = viewModel

            binding.diseaseNameTextView.apply {
                text = symptom.name
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                binding.diseaseDescriptionTextView.text =
                    Html.fromHtml(symptom.description, Html.FROM_HTML_MODE_LEGACY)
            } else {
                binding.diseaseDescriptionTextView.text =
                    Html.fromHtml(symptom.description)
            }
        }

        companion object {
            fun from (parent: ViewGroup) : ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemWikiBinding.inflate(inflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val wiki = wikiList[position]
        holder.bind(wiki, viewModel)
    }

    override fun getItemCount(): Int = wikiList.size

    fun setData(newList: List<Symptom>) {
        val onboardDiffUtil = RecyclerDIffUtil(wikiList, newList)
        val diffUtilResult = DiffUtil.calculateDiff(onboardDiffUtil)
        wikiList = newList
        diffUtilResult.dispatchUpdatesTo(this)
    }
}