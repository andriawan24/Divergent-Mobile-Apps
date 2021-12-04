package com.andriawan.divergent_mobile_apps.adapters

import android.annotation.SuppressLint
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.andriawan.divergent_mobile_apps.R
import com.andriawan.divergent_mobile_apps.databinding.ItemArticlesBinding
import com.andriawan.divergent_mobile_apps.databinding.ItemDiagnoseDiseaseBinding
import com.andriawan.divergent_mobile_apps.models.OnboardModel
import com.andriawan.divergent_mobile_apps.models.articles.Article
import com.andriawan.divergent_mobile_apps.models.diagnose.response.Disease
import com.andriawan.divergent_mobile_apps.models.diagnose.response.DiseasesPossibility
import com.andriawan.divergent_mobile_apps.utils.RecyclerDIffUtil
import java.math.RoundingMode
import kotlin.math.roundToInt

class DiseaseAdapter: RecyclerView.Adapter<DiseaseAdapter.ViewHolder>() {

    private var diseaseList = emptyList<DiseasesPossibility>()

    class ViewHolder(private val binding: ItemDiagnoseDiseaseBinding): RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(disease: DiseasesPossibility) {
            binding.disease = disease

            val possibility = disease.possibility.replace("%", "").toDouble()
            val rounded = possibility.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()

            when (rounded) {
                in 0F..30F -> {
                    binding.topViewCardView.apply {
                        backgroundTintList =
                            ContextCompat.getColorStateList(context, R.color.custom_color_subtle_green)
                    }

                    binding.bottomViewCardView.apply {
                        backgroundTintList =
                            ContextCompat.getColorStateList(context, R.color.custom_color_subtle_green)
                    }

                    binding.diseasePossibilityTextView.apply {
                        setTextColor(ContextCompat.getColor(context, R.color.custom_color_green))
                    }

                    binding.possibilityProgressBar.apply {
                        setIndicatorColor(ContextCompat.getColor(context, R.color.custom_color_green))
                    }
                }

                in 31F..50F -> {
                    binding.topViewCardView.apply {
                        backgroundTintList =
                            ContextCompat.getColorStateList(context, R.color.custom_color_light_purple_card)
                    }

                    binding.bottomViewCardView.apply {
                        backgroundTintList =
                            ContextCompat.getColorStateList(context, R.color.custom_color_light_purple_card)
                    }

                    binding.diseasePossibilityTextView.apply {
                        setTextColor(ContextCompat.getColor(context, R.color.custom_color_purple))
                    }

                    binding.possibilityProgressBar.apply {
                        setIndicatorColor(ContextCompat.getColor(context, R.color.custom_color_purple))
                    }
                }

                else -> {
                    binding.topViewCardView.apply {
                        backgroundTintList =
                            ContextCompat.getColorStateList(context, R.color.custom_color_subtle_pink)
                    }

                    binding.bottomViewCardView.apply {
                        backgroundTintList =
                            ContextCompat.getColorStateList(context, R.color.custom_color_subtle_pink)
                    }

                    binding.diseasePossibilityTextView.apply {
                        setTextColor(ContextCompat.getColor(context, R.color.custom_color_pink))
                    }

                    binding.possibilityProgressBar.apply {
                        setIndicatorColor(ContextCompat.getColor(context, R.color.custom_color_pink))
                    }
                }
            }

            binding.diseasePossibilityTextView.text = "Possibility $rounded%"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                binding.description.text =
                    Html.fromHtml(disease.disease.description, Html.FROM_HTML_MODE_COMPACT)
            } else {
                binding.description.text =
                    Html.fromHtml(disease.disease.description)
            }

            binding.possibilityProgressBar.progress = rounded.roundToInt()

            binding.topViewCardView.apply {
                setOnClickListener {
                    if (binding.dropdownImageView.tag == "unClick") {
                        binding.dropdownImageView.rotation = 180F
                        binding.bottomViewCardView.visibility = VISIBLE
                        binding.dropdownImageView.tag = "clicked"
                    } else {
                        binding.dropdownImageView.rotation = 0F
                        binding.bottomViewCardView.visibility = GONE
                        binding.dropdownImageView.tag = "unClick"
                    }
                }
            }
        }

        companion object {
            fun from (parent: ViewGroup) : ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemDiagnoseDiseaseBinding.inflate(inflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val disease = diseaseList[position]
        holder.bind(disease)
    }

    override fun getItemCount(): Int {
        return diseaseList.size
    }

    fun setData(newList: List<DiseasesPossibility>) {
        val onboardDiffUtil = RecyclerDIffUtil(diseaseList, newList)
        val diffUtilResult = DiffUtil.calculateDiff(onboardDiffUtil)
        diseaseList = newList
        diffUtilResult.dispatchUpdatesTo(this)
    }
}