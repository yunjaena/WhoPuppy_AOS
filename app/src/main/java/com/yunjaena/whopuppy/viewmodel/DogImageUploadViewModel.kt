package com.yunjaena.whopuppy.viewmodel

import android.net.Uri
import com.yunjaena.whopuppy.base.viewmodel.ViewModelBase
import com.yunjaena.whopuppy.data.CommunityRepository
import com.yunjaena.whopuppy.data.response.toCommonResponse
import com.yunjaena.whopuppy.util.SingleLiveEvent
import com.yunjaena.whopuppy.util.handleHttpException
import com.yunjaena.whopuppy.util.handleProgress
import com.yunjaena.whopuppy.util.toFile
import com.yunjaena.whopuppy.util.withThread
import io.reactivex.rxjava3.kotlin.addTo

class DogImageUploadViewModel(
    private val communityRepository: CommunityRepository
) : ViewModelBase() {
    private var imageUri: Uri? = null
    var successImageUploadEvent = SingleLiveEvent<String>()
    var failUploadImageMessageEvent = SingleLiveEvent<String>()

    fun setImage(uri: Uri) {
        imageUri = uri
    }

    fun uploadImage() {
        if (imageUri == null) {
            failUploadImageMessageEvent.call()
            return
        }
        val imageFile = imageUri!!.toFile()
        communityRepository.uploadImage(imageFile)
            .handleProgress(this)
            .handleHttpException()
            .withThread()
            .subscribe({
                successImageUploadEvent.value = it
            }) {
                it.toCommonResponse()?.also { response ->
                    failUploadImageMessageEvent.value = response.errorMessage
                }
            }.addTo(compositeDisposable)
    }
}
