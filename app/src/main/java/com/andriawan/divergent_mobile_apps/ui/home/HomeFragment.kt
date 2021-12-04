package com.andriawan.divergent_mobile_apps.ui.home

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.andriawan.divergent_mobile_apps.adapters.ArticleAdapter
import com.andriawan.divergent_mobile_apps.base.BaseFragment
import com.andriawan.divergent_mobile_apps.databinding.FragmentHomeBinding
import com.andriawan.divergent_mobile_apps.ui.login.LoginFragmentDirections
import com.andriawan.divergent_mobile_apps.utils.DialogBase
import com.andriawan.divergent_mobile_apps.utils.NetworkResult
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

    override fun onInitViews() {
        super.onInitViews()
        initRecyclerView()
        initDialog()

        binding.listener = viewModel
    }

    override fun onResume() {
        super.onResume()
        viewModel.getUser()
    }

    private fun initDialog() {
        dialogBase = DialogBase(requireContext(), layoutInflater)
    }

    private fun initRecyclerView() {
        adapter = ArticleAdapter()
        binding.articlesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.articlesRecyclerView.adapter = adapter
    }

    override fun onInitObservers() {
        super.onInitObservers()

        viewModel.goToProfile.observe(this, {
            it.getContentIfNotHandled()?.let {
                findNavController().navigate(HomeFragmentDirections
                    .actionHomeFragmentToUpdateProfileFragment())
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
    }
}