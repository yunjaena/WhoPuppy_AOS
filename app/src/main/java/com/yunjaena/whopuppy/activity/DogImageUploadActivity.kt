package com.yunjaena.whopuppy.activity

import android.net.Uri
import android.os.Bundle
import com.bumptech.glide.Glide
import com.yunjaena.whopuppy.R
import com.yunjaena.whopuppy.base.activity.ViewBindingActivity
import com.yunjaena.whopuppy.databinding.ActivityDogImageUploadBinding
import com.yunjaena.whopuppy.util.setOnSingleClickListener
import com.yunjaena.whopuppy.util.showToast
import com.yunjaena.whopuppy.viewmodel.DogImageUploadViewModel
import gun0912.tedimagepicker.builder.TedImagePicker
import org.koin.androidx.viewmodel.ext.android.viewModel

class DogImageUploadActivity : ViewBindingActivity<ActivityDogImageUploadBinding>() {
    override val layoutId: Int = R.layout.activity_dog_image_upload
    private val dogImageUploadViewModel: DogImageUploadViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        setBaseAppBar(getString(R.string.breed_analysis))
        setBackKey()
        useDefaultLoading(dogImageUploadViewModel)
        initView()
        initObserver()
    }

    private fun initView() {
        binding.dogImageView.setOnClickListener {
            TedImagePicker.with(this@DogImageUploadActivity)
                .start { uri -> setImage(uri) }
        }

        binding.breedCheckButton.setOnSingleClickListener {
            dogImageUploadViewModel.uploadImage()
        }
    }

    private fun setImage(uri: Uri) {
        Glide.with(this@DogImageUploadActivity)
            .load(uri)
            .into(binding.dogImageView)

        dogImageUploadViewModel.setImage(uri)
    }

    private fun initObserver() {
        with(dogImageUploadViewModel) {
            successImageUploadEvent.observe(this@DogImageUploadActivity) {
                if (it != null)
                    showToast(it)
            }

            failUploadImageMessageEvent.observe(this@DogImageUploadActivity) {
                when {
                    it != null -> showToast(it)
                    else -> showToast(getString(R.string.please_upload_image))
                }
            }
        }
    }
}
