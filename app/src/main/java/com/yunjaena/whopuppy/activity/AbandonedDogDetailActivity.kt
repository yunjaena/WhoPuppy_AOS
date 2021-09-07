package com.yunjaena.whopuppy.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.bumptech.glide.Glide
import com.yunjaena.whopuppy.R
import com.yunjaena.whopuppy.base.activity.ViewBindingActivity
import com.yunjaena.whopuppy.data.entity.AbandonedDogItem
import com.yunjaena.whopuppy.databinding.ActivityAbandonedDogDetailBinding
import com.yunjaena.whopuppy.util.showToast
import com.yunjaena.whopuppy.viewmodel.AbandonedDogDetailViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AbandonedDogDetailActivity : ViewBindingActivity<ActivityAbandonedDogDetailBinding>() {
    override val layoutId: Int = R.layout.activity_abandoned_dog_detail
    private val abandonedDogDetailViewModel: AbandonedDogDetailViewModel by viewModel()
    private var index: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        index = intent.getLongExtra(EXTRA_ABANDONED_ITEM_INDEX, -1)

        if (isWrongIndex()) {
            showToast(getString(R.string.wrong_access))
            finish()
            return
        }

        init()
        abandonedDogDetailViewModel.getAnimalDetail(index!!)
    }

    private fun isWrongIndex(): Boolean {
        return index == null || index == -1L
    }

    private fun init() {
        setAppBar()
        initObserver()
    }

    private fun setAppBar() {
        setBaseAppBar(getString(R.string.abandoned_dog_info))
        setBackKey()
    }

    private fun initObserver() {
        with(abandonedDogDetailViewModel) {
            animalDetail.observe(this@AbandonedDogDetailActivity) {
                initAbandonedDogView(it)
            }

            abandonedDogItemFetchFailMessage.observe(this@AbandonedDogDetailActivity) {
                if (it != null)
                    showToast(it)
            }
        }
    }

    private fun initAbandonedDogView(abandonedDogItem: AbandonedDogItem) {
        with(abandonedDogItem) {
            setCallFloatButton(careTel)
            setAnimalImage(popfile)
            setPhoneNumber(careTel)
            setSex(sexCd)
            setAge(age)
            setAnimalColor(colorCd)
            setProtectStatus(processState)
            setProtectPlace(careNm)
            setProtectPlaceAddress(careAddr)
            setAnimalStatus(specialMark)
            setAnimalKind(kindCd)
        }
    }

    private fun setCallFloatButton(phoneNumber: String) {
        binding.floatingButton.setOnClickListener {
            Intent().run {
                action = Intent.ACTION_DIAL
                data = Uri.parse("tel: $phoneNumber")
                startActivity(this)
            }
        }
    }

    private fun setAnimalImage(fileUrl: String) {
        Glide.with(this)
            .load(fileUrl)
            .into(binding.animalImageView)
    }

    private fun setPhoneNumber(phoneNumber: String) {
        binding.phoneNumberTextView.text = getString(R.string.phone_number_format, phoneNumber)
    }

    private fun setSex(sex: String) {
        binding.sex.text = getString(R.string.sex_format, sex)
    }

    private fun setAge(age: String) {
        binding.age.text = getString(R.string.age_format, age)
    }

    private fun setAnimalColor(color: String) {
        binding.animalColor.text = getString(R.string.color_format, color)
    }

    private fun setProtectStatus(protectStatus: String) {
        binding.processStatus.text = getString(R.string.protect_status_format, protectStatus)
    }

    private fun setProtectPlace(protectPlace: String) {
        binding.carePlaceName.text = getString(R.string.protect_place_format, protectPlace)
    }

    private fun setProtectPlaceAddress(protectPlaceAddress: String) {
        binding.carePlaceAddress.text =
            getString(R.string.protect_place_address_format, protectPlaceAddress)
    }

    private fun setAnimalStatus(status: String) {
        binding.currentStatus.text =
            getString(R.string.animal_status_format, status)
    }

    private fun setAnimalKind(kind: String) {
        binding.animalKind.text = getString(R.string.animal_kind_format, kind)
    }

    companion object {
        const val EXTRA_ABANDONED_ITEM_INDEX = "EXTRA_ABANDONED_ITEM_INDEX"
    }
}
