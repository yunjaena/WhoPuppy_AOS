package com.yunjaena.whopuppy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yunjaena.whopuppy.base.viewmodel.ViewModelBase
import com.yunjaena.whopuppy.data.AnimalRepository
import com.yunjaena.whopuppy.data.entity.DogBreedAnalysis
import com.yunjaena.whopuppy.data.response.toCommonResponse
import com.yunjaena.whopuppy.util.SingleLiveEvent
import com.yunjaena.whopuppy.util.handleHttpException
import com.yunjaena.whopuppy.util.handleProgress
import com.yunjaena.whopuppy.util.withThread
import io.reactivex.rxjava3.kotlin.addTo

class BreedCheckViewModel(
    private val animalRepository: AnimalRepository
) : ViewModelBase() {
    val animalBreedInfo: LiveData<List<DogBreedAnalysis>>
        get() = _animalBreedInfo
    private val _animalBreedInfo = MutableLiveData<List<DogBreedAnalysis>>()
    val animalBreedInfoFetchFailEvent = SingleLiveEvent<String>()

    fun findAnimalBreed(imageUrl: String) {
        if (animalBreedInfo.value != null) return
        animalRepository.getDogBreedInfo(imageUrl)
            .handleHttpException()
            .handleProgress(this)
            .withThread()
            .subscribe({
                _animalBreedInfo.value = it
            }) {
                it.toCommonResponse()?.also { response ->
                    animalBreedInfoFetchFailEvent.value = response.errorMessage
                }
            }.addTo(compositeDisposable)
    }
}
