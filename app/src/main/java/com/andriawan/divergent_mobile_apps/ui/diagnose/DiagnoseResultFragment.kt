package com.andriawan.divergent_mobile_apps.ui.diagnose

import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.andriawan.divergent_mobile_apps.R
import com.andriawan.divergent_mobile_apps.adapters.DiseaseAdapter
import com.andriawan.divergent_mobile_apps.base.BaseFragment
import com.andriawan.divergent_mobile_apps.databinding.FragmentDiagnoseResultBinding
import com.andriawan.divergent_mobile_apps.utils.NetworkResult
import dagger.hilt.android.AndroidEntryPoint
import android.content.Intent
import android.net.Uri
import com.andriawan.divergent_mobile_apps.utils.Constants.Companion.PHONE_NUMBER
import com.shashank.sony.fancytoastlib.FancyToast
import timber.log.Timber
import java.lang.Exception
import java.net.URLEncoder

@AndroidEntryPoint
class DiagnoseResultFragment : BaseFragment<FragmentDiagnoseResultBinding, SharedDiagnoseViewModel>() {

    override val viewModel: SharedDiagnoseViewModel by hiltNavGraphViewModels(R.id.nav_graph_diagnose)
    override val binding: FragmentDiagnoseResultBinding by lazy {
        FragmentDiagnoseResultBinding.inflate(layoutInflater)
    }

    private lateinit var adapter: DiseaseAdapter

    override fun onInitViews() {
        super.onInitViews()
        initRecyclerView()

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.consultMaterialButton.setOnClickListener {
            try {
                val packageManager = requireActivity().packageManager
                val i = Intent(Intent.ACTION_VIEW)
                val url =
                    "https://api.whatsapp.com/send?phone=$PHONE_NUMBER&text=" + URLEncoder.encode(
                        "Hi, I'd like to schedule a consultation",
                        "UTF-8"
                    )
                i.setPackage("com.whatsapp")
                i.data = Uri.parse(url)
                if (i.resolveActivity(packageManager) != null) {
                    startActivity(i)
                } else {
                    showToast("You don't have whatsapp installed", FancyToast.WARNING)
                }
            } catch (e: Exception) {
                showToast("You don't have whatsapp installed", FancyToast.WARNING)
                Timber.e(e)
            }
        }

        binding.backToHomeMaterialButton.setOnClickListener {
            findNavController()
                .navigate(DiagnoseResultFragmentDirections.actionDiagnoseResultCloseAllPage())
        }
    }

    private fun initRecyclerView() {
        adapter = DiseaseAdapter()
        binding.diseaseRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.diseaseRecyclerView.adapter = adapter
    }

    override fun onInitObservers() {
        super.onInitObservers()

        viewModel.diagnose.observe(this, {
            when (it) {
                is NetworkResult.Success -> {
                    it.data?.data?.diseases_possibilities?.let { models ->
                        adapter.setData(models.take(3))
                    }
                }

                is NetworkResult.Loading -> {
                    // No-ops
                }

                is NetworkResult.Error -> {
                    // No-ops
                }
            }
        })
    }
}