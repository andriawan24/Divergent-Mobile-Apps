package com.andriawan.divergent_mobile_apps.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.andriawan.divergent_mobile_apps.R
import com.andriawan.divergent_mobile_apps.base.BaseFragment
import com.andriawan.divergent_mobile_apps.databinding.FragmentUpdateProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentUpdateProfileBinding, ProfileViewModel>() {

    override val viewModel: ProfileViewModel by viewModels()
    override val binding: FragmentUpdateProfileBinding by lazy {
        FragmentUpdateProfileBinding.inflate(layoutInflater)
    }

    override fun onInitViews() {
        super.onInitViews()

        binding.viewModel = viewModel
        binding.listener = viewModel
    }

    override fun onInitObservers() {
        super.onInitObservers()

        viewModel.users.observe(this, {
            it.getContentIfNotHandled()?.let { user ->
                binding.user = user
            }
        })

        viewModel.goToLoginPage.observe(this, {
            it.getContentIfNotHandled()?.let {
                findNavController().navigate(
                    ProfileFragmentDirections.actionUpdateProfileFragmentToLoginFragment()
                )
            }
        })
    }
}