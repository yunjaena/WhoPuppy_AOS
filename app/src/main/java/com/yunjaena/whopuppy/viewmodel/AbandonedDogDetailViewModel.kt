package com.yunjaena.whopuppy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yunjaena.whopuppy.base.viewmodel.ViewModelBase
import com.yunjaena.whopuppy.data.AnimalRepository
import com.yunjaena.whopuppy.data.entity.AbandonedAnimalItem
import com.yunjaena.whopuppy.data.response.toCommonResponse
import com.yunjaena.whopuppy.util.SingleLiveEvent
import com.yunjaena.whopuppy.util.handleProgress
import com.yunjaena.whopuppy.util.withThread
import io.reactivex.rxjava3.kotlin.addTo

class AbandonedDogDetailViewModel(
    private val animalRepository: AnimalRepository
) : ViewModelBase() {
    val animalDetail: LiveData<AbandonedAnimalItem>
        get() = _animalDetail
    private val _animalDetail = MutableLiveData<AbandonedAnimalItem>()
    val abandonedDogItemFetchFailMessage = SingleLiveEvent<String>()

    fun getAnimalDetail(index: Long) {
        if (animalDetail.value != null) return

        animalRepository.getAnimalDetail(index)
            .withThread()
            .handleProgress(this)
            .subscribe({
                _animalDetail.value = it
            }) {
                it.toCommonResponse()?.also { response ->
                    abandonedDogItemFetchFailMessage.value = response.errorMessage
                }
            }.addTo(compositeDisposable)
    }
}
