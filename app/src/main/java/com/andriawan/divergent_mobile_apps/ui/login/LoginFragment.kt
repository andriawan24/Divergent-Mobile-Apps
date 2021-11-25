package com.andriawan.divergent_mobile_apps.ui.login

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.andriawan.divergent_mobile_apps.base.BaseFragment
import com.andriawan.divergent_mobile_apps.databinding.FragmentLoginBinding
import com.andriawan.divergent_mobile_apps.utils.NetworkResult
import com.shashank.sony.fancytoastlib.FancyToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>() {

    override val viewModel: LoginViewModel by viewModels()
    override val binding: FragmentLoginBinding by lazy {
        FragmentLoginBinding.inflate(layoutInflater)
    }

    private val args: LoginFragmentArgs by navArgs()

    override fun onInitViews() {
        super.onInitViews()

        if (args.toRegister) {
            toRegister()
        }

        binding.listener = viewModel
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    private fun toRegister() {
        findNavController().navigate(
            LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
        )
    }

    override fun onInitObservers() {
        super.onInitObservers()

        viewModel.loginResponse.observe(this, {
            when (it) {
                is NetworkResult.Loading -> {
                    binding.signInMaterialButton.isEnabled = false
                    showToast("Please wait...", FancyToast.INFO)
                }

                is NetworkResult.Success -> {
                    binding.signInMaterialButton.isEnabled = true
                    showToast("Success Login ${it.data?.data?.user?.name}", FancyToast.SUCCESS)
                }

                is NetworkResult.Error -> {
                    binding.signInMaterialButton.isEnabled = true
                    showToast("Error Login ${it.message}", FancyToast.ERROR)
                }
            }
        })

        viewModel.goRegister.observe(this, {
            it.getContentIfNotHandled()?.let {
                findNavController().navigate(LoginFragmentDirections
                    .actionLoginFragmentToRegisterFragment())
            }
        })
    }
}