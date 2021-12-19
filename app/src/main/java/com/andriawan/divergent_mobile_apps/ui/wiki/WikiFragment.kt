package com.andriawan.divergent_mobile_apps.ui.wiki

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.adapters.TextViewBindingAdapter
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.andriawan.divergent_mobile_apps.R
import com.andriawan.divergent_mobile_apps.adapters.WikiAdapter
import com.andriawan.divergent_mobile_apps.base.BaseFragment
import com.andriawan.divergent_mobile_apps.databinding.FragmentWikiBinding
import com.andriawan.divergent_mobile_apps.ui.articles.ArticlesFragment
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
class WikiFragment : BaseFragment<FragmentWikiBinding, WikiViewModel>() {

    override val viewModel: WikiViewModel by viewModels()
    override val binding: FragmentWikiBinding by lazy {
        FragmentWikiBinding.inflate(layoutInflater)
    }

    private lateinit var adapter: WikiAdapter
    private lateinit var dialogBase: DialogBase
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onInitViews() {
        super.onInitViews()
        initRecycler()
        initDialog()
        initSearchInput()

        firebaseAnalytics = Firebase.analytics
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_CLASS, ArticlesFragment::class.java.name)
            param(FirebaseAnalytics.Param.SCREEN_NAME, "ArticlesFragment")
        }

        binding.backImageView.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initSearchInput() {
        binding.edtSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.onSearchSubmitted(binding.edtSearch.text.toString())
                return@setOnEditorActionListener true
            }

            return@setOnEditorActionListener false
        }

        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No-ops
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    binding.edtSearchLayout.setEndIconTintList(
                        ContextCompat.getColorStateList(
                            requireContext(),
                            R.color.subtitle_text_color
                        )
                    )
                } else {
                    binding.edtSearchLayout.setEndIconTintList(
                        ContextCompat.getColorStateList(
                            requireContext(),
                            R.color.custom_color_purple
                        )
                    )
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // No-ops
            }

        })

        binding.edtSearchLayout.setEndIconOnClickListener {
            viewModel.onSearchSubmitted(binding.edtSearch.text.toString())
        }
    }

    private fun initDialog() {
        dialogBase = DialogBase(requireContext(), layoutInflater)
    }

    private fun initRecycler() {
        adapter = WikiAdapter(viewModel)
        binding.recyclerWiki.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerWiki.adapter = adapter
    }

    override fun onInitObservers() {
        super.onInitObservers()

        viewModel.wikiResponse.observe(this, {
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
                binding.emptyView.visibility =
                    if (adapter.itemCount == 0) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
            }
        })

        viewModel.showToast.observe(this, {
            it.getContentIfNotHandled()?.let { msg ->
                showToast(msg, FancyToast.SUCCESS)
            }
        })

        viewModel.onClickSymptom.observe(this, {
            it.getContentIfNotHandled()?.let { data ->
                findNavController().navigate(
                    WikiFragmentDirections.actionWikiFragmentToDetailWikiFragment(
                        Gson().toJson(data)
                    )
                )
            }
        })
    }
}