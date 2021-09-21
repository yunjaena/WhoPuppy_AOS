package com.dicelab.whopuppy.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dicelab.whopuppy.base.viewmodel.ViewModelBase
import com.dicelab.whopuppy.data.CommunityRepository
import com.dicelab.whopuppy.data.response.toCommonResponse
import com.dicelab.whopuppy.util.SingleLiveEvent
import com.dicelab.whopuppy.util.handleHttpException
import com.dicelab.whopuppy.util.handleProgress
import com.dicelab.whopuppy.util.toFile
import com.dicelab.whopuppy.util.withThread
import io.reactivex.rxjava3.kotlin.addTo

class DogImageUploadViewModel(
    private val communityRepository: CommunityRepository
) : ViewModelBase() {
    val imageUri: LiveData<Uri?>
        get() = _imageUri
    private var _imageUri = MutableLiveData<Uri?>()
    var successImageUploadEvent = SingleLiveEvent<String>()
    var failUploadImageMessageEvent = SingleLiveEvent<String>()

    fun setImage(uri: Uri) {
        _imageUri.value = uri
    }

    fun uploadImage() {
        if (imageUri.value == null) {
            failUploadImageMessageEvent.call()
            return
        }
        val imageFile = imageUri.value!!.toFile()
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
