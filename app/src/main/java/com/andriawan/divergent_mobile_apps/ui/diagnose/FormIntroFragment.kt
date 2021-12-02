package com.andriawan.divergent_mobile_apps.ui.diagnose

import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.andriawan.divergent_mobile_apps.R
import com.andriawan.divergent_mobile_apps.base.BaseFragment
import com.andriawan.divergent_mobile_apps.databinding.FragmentFormIntroDiagnoseBinding
import com.andriawan.divergent_mobile_apps.utils.DialogBase
import com.andriawan.divergent_mobile_apps.utils.NetworkResult
import com.shashank.sony.fancytoastlib.FancyToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FormIntroFragment : BaseFragment<FragmentFormIntroDiagnoseBinding, SharedDiagnoseViewModel>() {

    override val viewModel: SharedDiagnoseViewModel by hiltNavGraphViewModels(R.id.nav_graph_diagnose)
    override val binding: FragmentFormIntroDiagnoseBinding by lazy {
        FragmentFormIntroDiagnoseBinding.inflate(layoutInflater)
    }

    private lateinit var dialogBase: DialogBase

    override fun onInitViews() {
        super.onInitViews()
        initDialog()

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.getSymptoms()
    }

    private fun initDialog() {
        dialogBase = DialogBase(requireContext(), layoutInflater)
        dialogBase.setOnConfirmClicked {
            if (dialogBase.dialogState == DialogBase.ERROR) {
                findNavController().navigateUp()
            }

            dialogBase.dismiss()
        }
    }

    override fun onInitObservers() {
        super.onInitObservers()

        viewModel.showToast.observe(this, {
            it.getContentIfNotHandled()?.let { message ->
                showToast(message, FancyToast.SUCCESS)
            }
        })

        viewModel.symptoms.observe(this, {
            when (it) {
                is NetworkResult.Loading -> {
                    dialogBase.updateState(Pair(DialogBase.LOADING, ""))
                }

                is NetworkResult.Success -> {
                    dialogBase.dismiss()
                }

                is NetworkResult.Error -> {
                    dialogBase.updateState(Pair(DialogBase.ERROR, it.message))
                }
            }
        })
    }
}