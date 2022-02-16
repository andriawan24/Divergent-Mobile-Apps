package com.andriawan.divergent_mobile_apps.ui.detail_diagnose

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.andriawan.divergent_mobile_apps.R
import com.andriawan.divergent_mobile_apps.adapters.DiseaseAdapter
import com.andriawan.divergent_mobile_apps.adapters.DiseaseHistoryAdapter
import com.andriawan.divergent_mobile_apps.base.BaseFragment
import com.andriawan.divergent_mobile_apps.databinding.FragmentDetailDiagnoseBinding
import com.andriawan.divergent_mobile_apps.models.history_diagnose.Consultation
import com.andriawan.divergent_mobile_apps.utils.NetworkResult
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailDiagnoseFragment : BaseFragment<FragmentDetailDiagnoseBinding, DetailDiagnoseViewModel>() {

    override val viewModel: DetailDiagnoseViewModel by viewModels()
    override val binding: FragmentDetailDiagnoseBinding by lazy {
        FragmentDetailDiagnoseBinding.inflate(layoutInflater)
    }

    private lateinit var adapter: DiseaseHistoryAdapter
    private val args: DetailDiagnoseFragmentArgs by navArgs()

    override fun onInitViews() {
        super.onInitViews()
        initRecyclerView()

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val data = Gson().fromJson(args.data, Consultation::class.java)
        viewModel.setDiagnose(data)

        binding.backImageView.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initRecyclerView() {
        adapter = DiseaseHistoryAdapter()
        binding.diseaseRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.diseaseRecyclerView.adapter = adapter
    }

    override fun onInitObservers() {
        super.onInitObservers()

        viewModel.diagnose.observe(this, {
            adapter.setData(it.results.sortedByDescending { model ->
                model.possibility.replace("%", "").toDouble()
            }.take(3))
        })
    }
}