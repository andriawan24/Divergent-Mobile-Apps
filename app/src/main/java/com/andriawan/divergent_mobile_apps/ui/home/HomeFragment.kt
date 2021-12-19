package com.andriawan.divergent_mobile_apps.ui.home

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.andriawan.divergent_mobile_apps.adapters.ArticleAdapter
import com.andriawan.divergent_mobile_apps.base.BaseFragment
import com.andriawan.divergent_mobile_apps.databinding.FragmentHomeBinding
import com.andriawan.divergent_mobile_apps.ui.articles.ArticlesFragmentDirections
import com.andriawan.divergent_mobile_apps.ui.login.LoginFragmentDirections
import com.andriawan.divergent_mobile_apps.utils.DialogBase
import com.andriawan.divergent_mobile_apps.utils.NetworkResult
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.shashank.sony.fancytoastlib.FancyToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {

    override val viewModel: HomeViewModel by viewModels()
    override val binding: FragmentHomeBinding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
    }

    private lateinit var adapter: ArticleAdapter
    private lateinit var dialogBase: DialogBase
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onInitViews() {
        super.onInitViews()
        initRecyclerView()
        initDialog()

        binding.listener = viewModel
        firebaseAnalytics = Firebase.analytics
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_CLASS, HomeFragment::class.java.name)
            param(FirebaseAnalytics.Param.SCREEN_NAME, "HomeFragment")
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getUser()
    }

    private fun initDialog() {
        dialogBase = DialogBase(requireContext(), layoutInflater)
    }

    private fun initRecyclerView() {
        adapter = ArticleAdapter(viewModel)
        binding.articlesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.articlesRecyclerView.adapter = adapter
    }

    override fun onInitObservers() {
        super.onInitObservers()

        viewModel.goToProfile.observe(this, {
            it.getContentIfNotHandled()?.let {
                findNavController().navigate(HomeFragmentDirections
                    .actionHomeFragmentToUpdateProfileFragment())
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT) {
                    param(FirebaseAnalytics.Param.CONTENT, "Profile Menu")
                    param(FirebaseAnalytics.Param.CONTENT_TYPE, "menu")
                }
            }
        })

        viewModel.users.observe(this, {
            it.getContentIfNotHandled()?.let { user ->
                binding.user = user
            }
        })

        viewModel.goToDiagnose.observe(this, {
            it.getContentIfNotHandled()?.let {
                findNavController()
                    .navigate(HomeFragmentDirections
                        .actionHomeFragmentToFormIntroFragment())
            }
        })

        viewModel.goToDiagnoseHistory.observe(this, {
            it.getContentIfNotHandled()?.let {
                findNavController()
                    .navigate(HomeFragmentDirections
                        .actionHomeFragmentToHistoryFragment())
            }
        })

        viewModel.goToArticles.observe(this, {
            it.getContentIfNotHandled()?.let {
                findNavController()
                    .navigate(
                        HomeFragmentDirections.actionHomeFragmentToArticlesFragment()
                    )
            }
        })

        viewModel.goToWiki.observe(this, {
            it.getContentIfNotHandled()?.let {
                findNavController()
                    .navigate(
                        HomeFragmentDirections.actionHomeFragmentToWikiFragment()
                    )
            }
        })

        viewModel.showToast.observe(this, {
            it.getContentIfNotHandled()?.let { message ->
                showToast(message, FancyToast.SUCCESS)
            }
        })

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
                adapter.setData(data)
            }
        })

        viewModel.onClickNews.observe(this, {
            it.getContentIfNotHandled()?.let { article ->
                val articleJson = Gson().toJson(article)
                findNavController().navigate(
                    HomeFragmentDirections
                        .actionHomeFragmentToDetailArticleFragment(articleJson)
                )
            }
        })
    }
}