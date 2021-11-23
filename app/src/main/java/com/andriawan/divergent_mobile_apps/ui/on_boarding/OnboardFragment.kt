package com.andriawan.divergent_mobile_apps.ui.on_boarding

import android.view.View
import android.view.View.*
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
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

        viewModel.models.observe(this, {
            adapter.setData(it)
            addBottomDots(it.size)
        })
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

    override fun setOnClickListener() {
        binding.skipTextView.setOnClickListener(this)
        binding.nextTextView.setOnClickListener(this)
        binding.previousTextView.setOnClickListener(this)
        binding.signInMaterialButton.setOnClickListener(this)
        binding.signUpMaterialButton.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            binding.nextTextView.id -> binding.sliderViewPager.currentItem = binding.sliderViewPager.currentItem + 1
            binding.skipTextView.id -> {
                showToast("To Login", FancyToast.INFO)
            }
            binding.previousTextView.id -> binding.sliderViewPager.currentItem = binding.sliderViewPager.currentItem - 1
            binding.signInMaterialButton.id -> {
                showToast("To Login", FancyToast.INFO)
            }
            binding.signUpMaterialButton.id -> {
                showToast("To Register", FancyToast.INFO)
            }
        }
    }
}