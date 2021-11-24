package com.andriawan.divergent_mobile_apps.ui.on_boarding

import android.view.View.*
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.andriawan.divergent_mobile_apps.R
import com.andriawan.divergent_mobile_apps.adapters.OnboardAdapter
import com.andriawan.divergent_mobile_apps.base.BaseFragment
import com.andriawan.divergent_mobile_apps.databinding.FragmentOnboardingBinding
import com.andriawan.divergent_mobile_apps.utils.toDp
import com.shashank.sony.fancytoastlib.FancyToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardFragment: BaseFragment<FragmentOnboardingBinding, OnboardViewModel>() {

    override val viewModel: OnboardViewModel by viewModels()
    override val binding: FragmentOnboardingBinding by lazy {
        FragmentOnboardingBinding.inflate(layoutInflater)
    }

    private lateinit var adapter: OnboardAdapter
    private lateinit var dots: Array<ImageView?>

    override fun onInitViews() {
        super.onInitViews()
        setupViewPager()

        binding.listener = viewModel
    }

    private fun setupViewPager() {
        adapter = OnboardAdapter()
        binding.sliderViewPager.adapter = adapter
        binding.sliderViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateBottomDots(position)
                if (position == dots.size - 1) {
                    binding.skipTextView.visibility = INVISIBLE
                    binding.nextTextView.visibility = INVISIBLE
                    binding.previousTextView.visibility = INVISIBLE
                    binding.skipTextView.visibility = INVISIBLE
                    binding.finishLayout.visibility = VISIBLE
                } else {
                    binding.skipTextView.visibility = VISIBLE
                    binding.nextTextView.visibility = VISIBLE
                    if (position == 0) {
                        binding.previousTextView.visibility = INVISIBLE
                    } else {
                        binding.previousTextView.visibility = VISIBLE
                    }
                    binding.skipTextView.visibility = VISIBLE
                    binding.finishLayout.visibility = GONE
                }
            }
        })

        (binding.sliderViewPager.getChildAt(0) as RecyclerView).overScrollMode = OVER_SCROLL_NEVER
    }

    private fun updateBottomDots(position: Int) {
        for (i in dots.indices) {
            if (position == i) {
                dots[i]!!.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.dots_active))
            } else {
                dots[i]!!.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.dots_inactive))
            }
        }
    }

    override fun onInitObservers() {
        super.onInitObservers()

        viewModel.needToUpdateRecycler.observe(this, {
            it.getContentIfNotHandled()?.let { models ->
                adapter.setData(models)
                addBottomDots(models.size)
            }
        })

        viewModel.goToLoginPage.observe(this, {
            it.getContentIfNotHandled()?.let {
                toLoginPage()
            }
        })

        viewModel.goToRegisterPage.observe(this, {
            it.getContentIfNotHandled()?.let {
                toRegisterPage()
            }
        })

        viewModel.nextClicked.observe(this, {
            it.getContentIfNotHandled()?.let {
                binding.sliderViewPager.currentItem = binding.sliderViewPager.currentItem + 1
            }
        })

        viewModel.prevClicked.observe(this, {
            it.getContentIfNotHandled()?.let {
                binding.sliderViewPager.currentItem = binding.sliderViewPager.currentItem - 1
            }
        })

        viewModel.skipClicked.observe(this, {
            it.getContentIfNotHandled()?.let {
                toLoginPage()
            }
        })
    }

    private fun toRegisterPage() {
        showToast("To Register Page", FancyToast.SUCCESS)
    }

    private fun toLoginPage() {
        findNavController().navigate(OnboardFragmentDirections
            .actionOnboardFragmentToLoginFragment())
    }

    private fun addBottomDots(size: Int) {
        dots = arrayOfNulls(size)

        for (i in 0 until size) {
            dots[i] = ImageView(requireContext())
            dots[i]!!.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.dots_inactive))
            dots[i]!!.setPadding(2.toDp(), 0, 2.toDp(), 0)
            binding.dotsLinearLayout.addView(dots[i])
        }
    }
}