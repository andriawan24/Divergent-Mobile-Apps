package com.andriawan.divergent_mobile_apps.ui.login

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.andriawan.divergent_mobile_apps.base.BaseFragment
import com.andriawan.divergent_mobile_apps.databinding.FragmentLoginBinding
import com.andriawan.divergent_mobile_apps.utils.DialogBase
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
    private lateinit var dialogBase: DialogBase

    override fun onInitViews() {
        super.onInitViews()

        if (args.toRegister) {
            toRegister()
        }

        binding.listener = viewModel
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        initDialog()
    }

    private fun toRegister() {
        findNavController().navigate(
            LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
        )
    }

    private fun initDialog() {
        dialogBase = DialogBase(requireContext(), layoutInflater)
        dialogBase.setOnConfirmClicked {
            if (dialogBase.dialogState == DialogBase.SUCCESS) {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
            }
            dialogBase.dismiss()
        }
    }

    override fun onInitObservers() {
        super.onInitObservers()

        viewModel.loginResponse.observe(this, {
            when (it) {
                is NetworkResult.Loading -> {
                    dialogBase.updateState(Pair(DialogBase.LOADING, ""))
                    binding.signInMaterialButton.isEnabled = false
                }

                is NetworkResult.Success -> {
                    binding.signInMaterialButton.isEnabled = true
                    dialogBase.updateState(Pair(DialogBase.SUCCESS, "Success Login ${it.data?.data?.user?.name}"))
                }

                is NetworkResult.Error -> {
                    binding.signInMaterialButton.isEnabled = true
                    dialogBase.updateState(Pair(DialogBase.ERROR, "Error Login ${it.message}"))
                }
            }
        })

        viewModel.goToMainPage.observe(this, {
            it.getContentIfNotHandled()?.let {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
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