package com.andriawan.divergent_mobile_apps.ui.history

import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.andriawan.divergent_mobile_apps.R
import com.andriawan.divergent_mobile_apps.adapters.HistoryAdapter
import com.andriawan.divergent_mobile_apps.base.BaseFragment
import com.andriawan.divergent_mobile_apps.databinding.FragmentHistoryBinding
import com.andriawan.divergent_mobile_apps.utils.DialogBase
import com.andriawan.divergent_mobile_apps.utils.NetworkResult
import com.google.gson.Gson
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

        binding.listener = viewModel
        viewModel.getHistories()

        binding.backImageView.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initDialog() {
        dialogBase = DialogBase(requireContext(), layoutInflater)
    }

    private fun initRecyclerView() {
        adapter = HistoryAdapter(viewModel)
        binding.recyclerHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerHistory.adapter = adapter
    }

    override fun onInitObservers() {
        super.onInitObservers()

        viewModel.models.observe(this, {
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
            it.getContentIfNotHandled()?.let { model ->
                adapter.setData(model)
            }
        })

        viewModel.toDetail.observe(this, {
            it.getContentIfNotHandled()?.let { consultation ->
                findNavController()
                    .navigate(
                        HistoryFragmentDirections
                            .actionHistoryFragmentToDetailDiagnoseFragment(
                                Gson().toJson(consultation)
                            )
                    )
            }
        })

        viewModel.sortSelected.observe(this, {
            it.getContentIfNotHandled()?.let { text ->
                when (text) {
                    getString(R.string.newest) -> {
                        binding.buttonNewest.apply {
                            backgroundTintList = ContextCompat.getColorStateList(
                                requireContext(),
                                R.color.custom_color_purple
                            )

                            setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                        }

                        binding.buttonLongest.apply {
                            backgroundTintList = ContextCompat.getColorStateList(
                                requireContext(),
                                R.color.custom_color_subtle_grey
                            )

                            setTextColor(ContextCompat.getColor(requireContext(), R.color.custom_color_grey))
                        }

                        binding.buttonByName.apply {
                            backgroundTintList = ContextCompat.getColorStateList(
                                requireContext(),
                                R.color.custom_color_subtle_grey
                            )

                            setTextColor(ContextCompat.getColor(requireContext(), R.color.custom_color_grey))
                        }

                        viewModel.getHistories(SORT_NEWEST)
                    }

                    getString(R.string.longest) -> {
                        binding.buttonLongest.apply {
                            backgroundTintList = ContextCompat.getColorStateList(
                                requireContext(),
                                R.color.custom_color_purple
                            )

                            setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                        }

                        binding.buttonNewest.apply {
                            backgroundTintList = ContextCompat.getColorStateList(
                                requireContext(),
                                R.color.custom_color_subtle_grey
                            )

                            setTextColor(ContextCompat.getColor(requireContext(), R.color.custom_color_grey))
                        }

                        binding.buttonByName.apply {
                            backgroundTintList = ContextCompat.getColorStateList(
                                requireContext(),
                                R.color.custom_color_subtle_grey
                            )

                            setTextColor(ContextCompat.getColor(requireContext(), R.color.custom_color_grey))
                        }

                        viewModel.getHistories(SORT_LONGEST)
                    }

                    getString(R.string.by_name) -> {
                        binding.buttonByName.apply {
                            backgroundTintList = ContextCompat.getColorStateList(
                                requireContext(),
                                R.color.custom_color_purple
                            )

                            setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                        }

                        binding.buttonNewest.apply {
                            backgroundTintList = ContextCompat.getColorStateList(
                                requireContext(),
                                R.color.custom_color_subtle_grey
                            )

                            setTextColor(ContextCompat.getColor(requireContext(), R.color.custom_color_grey))
                        }

                        binding.buttonLongest.apply {
                            backgroundTintList = ContextCompat.getColorStateList(
                                requireContext(),
                                R.color.custom_color_subtle_grey
                            )

                            setTextColor(ContextCompat.getColor(requireContext(), R.color.custom_color_grey))
                        }

                        viewModel.getHistories(SORT_BY_NAME)
                    }
                }
            }
        })
    }

    companion object {
        const val SORT_NEWEST = "newest"
        const val SORT_LONGEST = "longest"
        const val SORT_BY_NAME = "by_name"
    }
}