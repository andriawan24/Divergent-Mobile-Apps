package com.andriawan.divergent_mobile_apps.ui.articles

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andriawan.divergent_mobile_apps.R
import com.andriawan.divergent_mobile_apps.adapters.ArticleAdapter
import com.andriawan.divergent_mobile_apps.adapters.BreakingNewsAdapter
import com.andriawan.divergent_mobile_apps.base.BaseFragment
import com.andriawan.divergent_mobile_apps.databinding.FragmentArticlesBinding
import com.andriawan.divergent_mobile_apps.ui.home.HomeFragment
import com.andriawan.divergent_mobile_apps.utils.DialogBase
import com.andriawan.divergent_mobile_apps.utils.NetworkResult
import com.google.android.material.tabs.TabLayout
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.shashank.sony.fancytoastlib.FancyToast
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ArticlesFragment : BaseFragment<FragmentArticlesBinding, ArticlesViewModel>() {
    override val viewModel: ArticlesViewModel by viewModels()
    override val binding: FragmentArticlesBinding by lazy {
        FragmentArticlesBinding.inflate(layoutInflater)
    }

    private lateinit var dialogBase: DialogBase
    private lateinit var adapter: ArticleAdapter
    private lateinit var adapterBreakingNews: BreakingNewsAdapter
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onInitViews() {
        super.onInitViews()
        initDialog()
        initRecyclerView()
        initTabLayout()

        firebaseAnalytics = Firebase.analytics
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_CLASS, ArticlesFragment::class.java.name)
            param(FirebaseAnalytics.Param.SCREEN_NAME, "ArticlesFragment")
        }

        binding.backImageView.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initTabLayout() {
        binding.tabLayoutCategory.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                Timber.d("Tag is ${tab?.tag}")
                if (tab?.tag.toString() != "null") {
                    viewModel.getArticles(tab?.tag.toString())
                } else {
                    viewModel.getArticles()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Do nothing
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Do nothing
            }
        })
    }

    private fun initDialog() {
        dialogBase = DialogBase(requireContext(), layoutInflater)
    }

    private fun initRecyclerView() {
        adapter = ArticleAdapter(viewModel)
        binding.articlesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.articlesRecyclerView.adapter = adapter

        adapterBreakingNews = BreakingNewsAdapter(viewModel)
        binding.breakingNewsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        binding.breakingNewsRecyclerView.adapter = adapterBreakingNews
    }

    override fun onInitObservers() {
        super.onInitObservers()

        viewModel.articleResponse.observe(this, {
            when (it) {
                is NetworkResult.Loading -> {
                    dialogBase.updateState(Pair(DialogBase.LOADING, ""))
                }

                is NetworkResult.Success -> {
                    dialogBase.dismiss()
                }

                is NetworkResult.Error -> {
                    dialogBase.updateState(Pair(DialogBase.ERROR, it.message))
                }
            }
        })

        viewModel.updateRecyclerView.observe(this, {
            it.getContentIfNotHandled()?.let { data ->
                if (adapterBreakingNews.itemCount == 0) {
                    adapterBreakingNews.setData(data.first)
                }
                adapter.setData(data.second)
                binding.emptyView.visibility =
                    if (adapter.itemCount == 0) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
            }
        })

        viewModel.updateArticleCategories.observe(this, {
            it.getContentIfNotHandled()?.let { data ->
                for (category in data) {
                    val tab = binding.tabLayoutCategory.newTab()
                    tab.text = category.name
                    tab.tag = category.id
                    binding.tabLayoutCategory.addTab(tab)
                }
            }
        })

        viewModel.showToast.observe(this, {
            it.getContentIfNotHandled()?.let { msg ->
                showToast(msg, FancyToast.SUCCESS)
            }
        })

        viewModel.onClickNews.observe(this, {
            it.getContentIfNotHandled()?.let { article ->
                val articleJson = Gson().toJson(article)
                findNavController().navigate(
                    ArticlesFragmentDirections
                        .actionArticlesFragmentToDetailArticleFragment(articleJson)
                )
            }
        })
    }
}