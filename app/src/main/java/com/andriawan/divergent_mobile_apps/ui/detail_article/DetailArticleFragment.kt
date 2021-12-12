package com.andriawan.divergent_mobile_apps.ui.detail_article

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
import com.andriawan.divergent_mobile_apps.databinding.FragmentDetailArticleBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailArticleFragment : BaseFragment<FragmentDetailArticleBinding, DetailArticleViewModel>() {
    override val viewModel: DetailArticleViewModel by viewModels()
    override val binding: FragmentDetailArticleBinding by lazy {
        FragmentDetailArticleBinding.inflate(layoutInflater)
    }

    private val args: DetailArticleFragmentArgs by navArgs()

    override fun onInitViews() {
        super.onInitViews()

        viewModel.setArticle(args.articleJson)
        binding.backImageView.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onInitObservers() {
        super.onInitObservers()

        viewModel.article.observe(this, {
            it.getContentIfNotHandled()?.let { article ->
                binding.article = article
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    binding.descriptionTextView.text =
                        Html.fromHtml(article.description, Html.FROM_HTML_OPTION_USE_CSS_COLORS)
                } else {
                    binding.descriptionTextView.text =
                        Html.fromHtml(article.description)
                }
            }
        })
    }
}