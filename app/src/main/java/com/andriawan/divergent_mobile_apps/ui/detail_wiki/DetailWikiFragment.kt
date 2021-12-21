package com.andriawan.divergent_mobile_apps.ui.detail_wiki

import android.os.Build
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.andriawan.divergent_mobile_apps.R
import com.andriawan.divergent_mobile_apps.base.BaseFragment
import com.andriawan.divergent_mobile_apps.databinding.FragmentDetailWikiBinding
import com.andriawan.divergent_mobile_apps.models.symptoms.response.Symptom
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailWikiFragment : BaseFragment<FragmentDetailWikiBinding, DetailWikiViewModel>() {

    override val viewModel: DetailWikiViewModel by viewModels()
    override val binding: FragmentDetailWikiBinding by lazy {
        FragmentDetailWikiBinding.inflate(layoutInflater)
    }

    private val args: DetailWikiFragmentArgs by navArgs()

    override fun onInitViews() {
        super.onInitViews()

        viewModel.setWikiData(Gson().fromJson(args.data, Symptom::class.java))

        binding.backImageView.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onInitObservers() {
        super.onInitObservers()

        viewModel.updateWiki.observe(this, {
            it.getContentIfNotHandled()?.let { data ->
                binding.article = data
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    binding.descriptionTextView.text =
                        Html.fromHtml(data.description, Html.FROM_HTML_OPTION_USE_CSS_COLORS)
                } else {
                    binding.descriptionTextView.text =
                        Html.fromHtml(data.description)
                }
            }
        })
    }
}