package com.andriawan.divergent_mobile_apps.ui.loading

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.andriawan.divergent_mobile_apps.base.BaseFragment
import com.andriawan.divergent_mobile_apps.databinding.FragmentLoadingBinding
import com.shashank.sony.fancytoastlib.FancyToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoadingFragment : BaseFragment<FragmentLoadingBinding, LoadingViewModel>() {

    override val viewModel: LoadingViewModel by viewModels()
    override val binding: FragmentLoadingBinding by lazy {
        FragmentLoadingBinding.inflate(layoutInflater)
    }

    override fun onInitViews() {
        super.onInitViews()

        lifecycleScope.launch {
            delay(5000)
            goToSplashScreen()
        }
    }

    private fun goToSplashScreen() {
        findNavController().navigate(
            LoadingFragmentDirections.actionLoadingFragmentToOnboardFragment()
        )
    }

    override fun onInitObservers() {
        super.onInitObservers()

        viewModel.isLoading.observe(this, {
            if (it) {
                showToast("Go to splash", FancyToast.SUCCESS)
            }
        })
    }

    override fun setOnClickListener() {
        // No-ops
    }

    override fun onClick(v: View?) {
        // No-ops
    }
}