package com.andriawan.divergent_mobile_apps.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.andriawan.divergent_mobile_apps.R
import com.andriawan.divergent_mobile_apps.databinding.ItemQuestionDiagnoseBinding
import com.andriawan.divergent_mobile_apps.models.symptoms.response.Symptom
import com.andriawan.divergent_mobile_apps.ui.diagnose.SharedDiagnoseViewModel
import com.andriawan.divergent_mobile_apps.utils.RecyclerDIffUtil
import timber.log.Timber

class QuestionDiagnoseAdapter(
    private val viewModel: SharedDiagnoseViewModel
): RecyclerView.Adapter<QuestionDiagnoseAdapter.ViewHolder>() {

    private var questionList = emptyList<Symptom>()

    class ViewHolder(private val binding: ItemQuestionDiagnoseBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            question: Symptom,
            position: Int,
            questionList: List<Symptom>,
            viewModel: SharedDiagnoseViewModel
        ) {
            binding.viewModel = viewModel

            binding.questionNumber.text =
                binding.root.context.getString(
                    R.string.question_number,
                    position+1,
                    questionList.size
                )

            binding.questionText.text = question.question

            var isClicked = false

            binding.yesButton.setOnClickListener {
                isClicked = true
                viewModel.answerYes(question.id)

                binding.yesButton.apply {
                    backgroundTintList = ContextCompat.getColorStateList(context, R.color.custom_color_purple)
                    setTextColor(ContextCompat.getColor(context, R.color.white))
                }

                binding.noButton.apply {
                    backgroundTintList = ContextCompat.getColorStateList(context, R.color.white)
                    setTextColor(ContextCompat.getColor(context, R.color.custom_color_grey))
                }
            }

            binding.noButton.setOnClickListener {
                isClicked = true
                viewModel.answerNo(question.id)

                binding.noButton.apply {
                    backgroundTintList = ContextCompat.getColorStateList(context, R.color.custom_color_purple)
                    setTextColor(ContextCompat.getColor(context, R.color.white))
                }

                binding.yesButton.apply {
                    backgroundTintList = ContextCompat.getColorStateList(context, R.color.white)
                    setTextColor(ContextCompat.getColor(context, R.color.custom_color_grey))
                }
            }

            binding.yesButton.apply {
                backgroundTintList = ContextCompat.getColorStateList(context, R.color.white)
                setTextColor(ContextCompat.getColor(context, R.color.custom_color_grey))
            }

            binding.noButton.apply {
                backgroundTintList = ContextCompat.getColorStateList(context, R.color.white)
                setTextColor(ContextCompat.getColor(context, R.color.custom_color_grey))
            }

            binding.nextButton.setOnClickListener {
                if (isClicked) {
                    val isLast = position == questionList.size - 1
                    viewModel.next(isLast)
                } else {
                    viewModel.showToast("Please enter an answer")
                }
            }

            if (position == questionList.size - 1) {
                binding.nextButton.text = binding.root.context.getString(R.string.submit_diagnose)
            }
        }

        companion object {
            fun from (parent: ViewGroup) : ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemQuestionDiagnoseBinding.inflate(inflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val question = questionList[position]
        holder.bind(question, position, questionList, viewModel)
    }

    override fun getItemCount(): Int = questionList.size

    fun setData(newList: List<Symptom>?) {
        newList?.let { symptoms ->
            val onboardDiffUtil = RecyclerDIffUtil(questionList, symptoms)
            val diffUtilResult = DiffUtil.calculateDiff(onboardDiffUtil)
            questionList = symptoms
            diffUtilResult.dispatchUpdatesTo(this)
        }
    }
}