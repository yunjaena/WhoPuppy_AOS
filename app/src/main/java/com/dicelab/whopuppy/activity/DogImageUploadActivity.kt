package com.dicelab.whopuppy.activity

import android.net.Uri
import android.os.Bundle
import com.bumptech.glide.Glide
import com.dicelab.whopuppy.R
import com.dicelab.whopuppy.base.activity.ViewBindingActivity
import com.dicelab.whopuppy.databinding.ActivityDogImageUploadBinding
import com.dicelab.whopuppy.util.goToBreedCheckActivity
import com.dicelab.whopuppy.util.setOnSingleClickListener
import com.dicelab.whopuppy.util.showToast
import com.dicelab.whopuppy.viewmodel.DogImageUploadViewModel
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
        dogImageUploadViewModel.setImage(uri)
    }

    private fun initObserver() {
        with(dogImageUploadViewModel) {
            successImageUploadEvent.observe(this@DogImageUploadActivity) {
                if (it != null)
                    goToBreedCheckActivity(it)
            }

            failUploadImageMessageEvent.observe(this@DogImageUploadActivity) {
                when {
                    it != null -> showToast(it)
                    else -> showToast(getString(R.string.please_upload_image))
                }
            }

            imageUri.observe(this@DogImageUploadActivity) {
                Glide.with(this@DogImageUploadActivity)
                    .load(it)
                    .into(binding.dogImageView)
            }
        }
    }
}
