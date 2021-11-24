package com.andriawan.divergent_mobile_apps.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.andriawan.divergent_mobile_apps.R
import com.andriawan.divergent_mobile_apps.base.BaseFragment
import com.andriawan.divergent_mobile_apps.databinding.FragmentLoginBinding
import com.andriawan.divergent_mobile_apps.ui.loading.LoadingViewModel
import com.andriawan.divergent_mobile_apps.utils.NetworkResult
import com.shashank.sony.fancytoastlib.FancyToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>() {

    override val viewModel: LoginViewModel by viewModels()
    override val binding: FragmentLoginBinding by lazy {
        FragmentLoginBinding.inflate(layoutInflater)
    }

    override fun onInitViews() {
        super.onInitViews()

        binding.listener = viewModel
    }

    override fun onInitObservers() {
        super.onInitObservers()

        viewModel.loginResponse.observe(this, {
            when (it) {
                is NetworkResult.Loading -> {
                    showToast("Loading", FancyToast.INFO)
                }

                is NetworkResult.Success -> {
                    showToast("Success Login ${it.data?.data?.user?.name}", FancyToast.SUCCESS)
                }

                is NetworkResult.Error -> {
                    showToast("Error Login ${it.message}", FancyToast.ERROR)
                }
            }
        })
    }
}