package com.andriawan.divergent_mobile_apps.ui.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.andriawan.divergent_mobile_apps.R
import com.andriawan.divergent_mobile_apps.base.BaseFragment
import com.andriawan.divergent_mobile_apps.databinding.FragmentRegisterBinding
import com.andriawan.divergent_mobile_apps.utils.NetworkResult
import com.shashank.sony.fancytoastlib.FancyToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : BaseFragment<FragmentRegisterBinding, RegisterViewModel>() {
    override val viewModel: RegisterViewModel by viewModels()
    override val binding: FragmentRegisterBinding by lazy {
        FragmentRegisterBinding.inflate(layoutInflater)
    }

    override fun onInitViews() {
        super.onInitViews()

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    override fun onInitObservers() {
        super.onInitObservers()

        viewModel.registerResponse.observe(this, {
            when (it) {
                is NetworkResult.Loading -> {
                    showToast("Loading", FancyToast.INFO)
                }

                is NetworkResult.Success -> {
                    showToast("Success register ${it.data?.data?.user?.name}", FancyToast.SUCCESS)
                }

                is NetworkResult.Error -> {
                    showToast("Error register ${it.message}", FancyToast.ERROR)
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