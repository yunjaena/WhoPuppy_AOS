package com.dicelab.whopuppy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dicelab.whopuppy.base.viewmodel.ViewModelBase
import com.dicelab.whopuppy.data.AnimalRepository
import com.dicelab.whopuppy.data.entity.AbandonedAnimalItem
import com.dicelab.whopuppy.data.response.toCommonResponse
import com.dicelab.whopuppy.util.SingleLiveEvent
import com.dicelab.whopuppy.util.handleProgress
import com.dicelab.whopuppy.util.withThread
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
