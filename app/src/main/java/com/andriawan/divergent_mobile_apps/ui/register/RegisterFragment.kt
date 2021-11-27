package com.andriawan.divergent_mobile_apps.ui.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.andriawan.divergent_mobile_apps.R
import com.andriawan.divergent_mobile_apps.base.BaseFragment
import com.andriawan.divergent_mobile_apps.databinding.FragmentRegisterBinding
import com.andriawan.divergent_mobile_apps.ui.login.LoginFragmentDirections
import com.andriawan.divergent_mobile_apps.utils.DialogBase
import com.andriawan.divergent_mobile_apps.utils.NetworkResult
import com.shashank.sony.fancytoastlib.FancyToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : BaseFragment<FragmentRegisterBinding, RegisterViewModel>() {
    override val viewModel: RegisterViewModel by viewModels()
    override val binding: FragmentRegisterBinding by lazy {
        FragmentRegisterBinding.inflate(layoutInflater)
    }

    private lateinit var dialogBase: DialogBase

    override fun onInitViews() {
        super.onInitViews()

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        initDialog()
    }

    private fun initDialog() {
        dialogBase = DialogBase(requireContext(), layoutInflater)
        dialogBase.setOnConfirmClicked {
            if (dialogBase.dialogState == DialogBase.SUCCESS) {
                findNavController().navigate(RegisterFragmentDirections
                    .actionRegisterFragmentToHomeFragment())
            }
            dialogBase.dismiss()
        }
    }

    override fun onInitObservers() {
        super.onInitObservers()

        viewModel.registerResponse.observe(this, {
            when (it) {
                is NetworkResult.Loading -> {
                    dialogBase.updateState(Pair(DialogBase.LOADING, ""))
                    binding.signUpMaterialButton.isEnabled = false
                }

                is NetworkResult.Success -> {
                    binding.signUpMaterialButton.isEnabled = true
                    dialogBase.updateState(Pair(DialogBase.SUCCESS, "Success Register, hello ${it.data?.data?.user?.name}"))
                }

                is NetworkResult.Error -> {
                    binding.signUpMaterialButton.isEnabled = true
                    dialogBase.updateState(Pair(DialogBase.ERROR, "Error Login ${it.message}"))
                }
            }
        })

        viewModel.showToast.observe(this, {
            it.getContentIfNotHandled()?.let {
                showToast("Success register", FancyToast.SUCCESS)
            }
        })
    }
}