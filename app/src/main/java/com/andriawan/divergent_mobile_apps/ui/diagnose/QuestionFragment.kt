package com.andriawan.divergent_mobile_apps.ui.diagnose

import androidx.activity.addCallback
import androidx.core.view.size
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.andriawan.divergent_mobile_apps.R
import com.andriawan.divergent_mobile_apps.adapters.QuestionDiagnoseAdapter
import com.andriawan.divergent_mobile_apps.base.BaseFragment
import com.andriawan.divergent_mobile_apps.databinding.FragmentQuestionDiagnoseBinding
import com.andriawan.divergent_mobile_apps.utils.ConfirmDialog
import com.andriawan.divergent_mobile_apps.utils.NetworkResult
import com.shashank.sony.fancytoastlib.FancyToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuestionFragment : BaseFragment<FragmentQuestionDiagnoseBinding, SharedDiagnoseViewModel>() {

    override val viewModel: SharedDiagnoseViewModel by hiltNavGraphViewModels(R.id.nav_graph_diagnose)
    override val binding: FragmentQuestionDiagnoseBinding by lazy {
        FragmentQuestionDiagnoseBinding.inflate(layoutInflater)
    }

    private lateinit var adapter: QuestionDiagnoseAdapter
    private var positionNow = 0
    private lateinit var confirmDialog: ConfirmDialog

    override fun onInitViews() {
        super.onInitViews()
        initRecyclerView()
        initDialog()

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (positionNow == 0) {
                viewModel.cancel()
            } else {
                binding.questionViewPager.currentItem = binding.questionViewPager.currentItem - 1
            }
        }
    }

    private fun initDialog() {
        confirmDialog = ConfirmDialog(requireContext(), layoutInflater)
        confirmDialog.setOnConfirmClicked {
            findNavController().navigate(
                QuestionFragmentDirections.actionFormQuestionCloseAllPage()
            )
            confirmDialog.dismiss()
        }
    }

    private fun initRecyclerView() {
        adapter = QuestionDiagnoseAdapter(viewModel)
        binding.questionViewPager.adapter = adapter
        binding.questionViewPager.isUserInputEnabled = false
        binding.questionViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                positionNow = position
            }
        })
    }

    override fun onInitObservers() {
        super.onInitObservers()

        viewModel.symptoms.observe(this, {
            when (it) {
                is NetworkResult.Success -> {
                    adapter.setData(it.data?.data?.symptoms)
                }

                is NetworkResult.Loading -> {
                    // no-ops
                }

                is NetworkResult.Error -> {
                    // no-ops
                }
            }
        })

        viewModel.showToast.observe(this, {
            it.getContentIfNotHandled()?.let { msg ->
                showToast(msg, FancyToast.SUCCESS)
            }
        })

        viewModel.nextClicked.observe(this, {
            it.getContentIfNotHandled()?.let {
                binding.questionViewPager.currentItem = binding.questionViewPager.currentItem + 1
            }
        })

        viewModel.cancelClicked.observe(this, {
            it.getContentIfNotHandled()?.let {
                confirmDialog.showDialog("Are you sure?", "You will lose your answer data")
            }
        })
    }
}