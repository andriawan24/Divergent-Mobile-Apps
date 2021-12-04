package com.andriawan.divergent_mobile_apps.ui.history

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.andriawan.divergent_mobile_apps.adapters.ArticleAdapter
import com.andriawan.divergent_mobile_apps.adapters.HistoryAdapter
import com.andriawan.divergent_mobile_apps.base.BaseFragment
import com.andriawan.divergent_mobile_apps.databinding.FragmentHistoryBinding
import com.andriawan.divergent_mobile_apps.utils.DialogBase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : BaseFragment<FragmentHistoryBinding, HistoryViewModel>() {

    override val viewModel: HistoryViewModel by viewModels()
    override val binding: FragmentHistoryBinding by lazy {
        FragmentHistoryBinding.inflate(layoutInflater)
    }

    private lateinit var adapter: HistoryAdapter
    private lateinit var dialogBase: DialogBase

    override fun onInitViews() {
        super.onInitViews()
        initRecyclerView()
        initDialog()

        binding.backImageView.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initDialog() {
        dialogBase = DialogBase(requireContext(), layoutInflater)
    }

    private fun initRecyclerView() {
        adapter = HistoryAdapter()
    }

    override fun onInitObservers() {
        super.onInitObservers()
    }
}