package com.andriawan.divergent_mobile_apps.ui.profile

import android.Manifest
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.andriawan.divergent_mobile_apps.R
import com.andriawan.divergent_mobile_apps.base.BaseFragment
import com.andriawan.divergent_mobile_apps.databinding.FragmentUpdateProfileBinding
import com.andriawan.divergent_mobile_apps.ui.register.RegisterFragmentDirections
import com.andriawan.divergent_mobile_apps.utils.DialogBase
import com.andriawan.divergent_mobile_apps.utils.GlideHelper
import com.andriawan.divergent_mobile_apps.utils.NetworkResult
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.shashank.sony.fancytoastlib.FancyToast
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentUpdateProfileBinding, ProfileViewModel>() {

    override val viewModel: ProfileViewModel by viewModels()
    override val binding: FragmentUpdateProfileBinding by lazy {
        FragmentUpdateProfileBinding.inflate(layoutInflater)
    }

    private lateinit var dialogBase: DialogBase
    private var bitmap: Bitmap? = null

    private val startForImageResultActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    val fileUri = data?.data

                    val file = File(fileUri?.path!!)
                    bitmap = BitmapFactory.decodeFile(file.path)

                    GlideHelper.showImageProfile(fileUri, binding.imageView, requireContext())
                    viewModel.updateImageFile(bitmap)
                }

                ImagePicker.RESULT_ERROR -> {
                    showToast(ImagePicker.getError(data), FancyToast.ERROR)
                }

                else -> {
                    Timber.e("Unknown Error")
                }
            }
        }

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            var isPermitted = false

            permissions.entries.forEach {
                isPermitted = it.value
            }

            if (isPermitted) {
                ImagePicker.Companion.with(requireActivity())
                    .compress(1024)
                    .cropSquare()
                    .maxResultSize(300, 300)
                    .createIntent { intent ->
                        startForImageResultActivity.launch(intent)
                    }
            } else {
                showToast("You have to accept permission to continue", FancyToast.WARNING)
            }
        }

    override fun onInitViews() {
        super.onInitViews()

        binding.viewModel = viewModel
        binding.listener = viewModel
        binding.lifecycleOwner = this

        initDialog()

        binding.backImageView.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.relative.setOnClickListener {
            activityResultLauncher.launch(
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
            )
        }
    }

    private fun initDialog() {
        dialogBase = DialogBase(requireContext(), layoutInflater)
        dialogBase.setOnConfirmClicked {
            dialogBase.dismiss()
        }
    }

    override fun onInitObservers() {
        super.onInitObservers()

        viewModel.users.observe(this, {
            it.getContentIfNotHandled()?.let { user ->
                binding.user = user
            }
        })

        viewModel.goToLoginPage.observe(this, {
            it.getContentIfNotHandled()?.let {
                findNavController().navigate(
                    ProfileFragmentDirections.actionUpdateProfileFragmentToLoginFragment()
                )
            }
        })

        viewModel.updateProfileResponse.observe(this, {
            when (it) {
                is NetworkResult.Loading -> {
                    dialogBase.updateState(Pair(DialogBase.LOADING, ""))
                    binding.saveButtonMaterialButton.isEnabled = false
                }

                is NetworkResult.Success -> {
                    binding.saveButtonMaterialButton.isEnabled = true
                    dialogBase.updateState(Pair(DialogBase.SUCCESS, "Profile updated successfully"))
                }

                is NetworkResult.Error -> {
                    binding.saveButtonMaterialButton.isEnabled = true
                    dialogBase.updateState(Pair(DialogBase.ERROR, "Error Login ${it.message}"))
                }
            }
        })
    }
}