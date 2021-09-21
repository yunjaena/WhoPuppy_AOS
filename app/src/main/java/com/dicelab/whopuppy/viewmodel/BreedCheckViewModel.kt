package com.dicelab.whopuppy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dicelab.whopuppy.base.viewmodel.ViewModelBase
import com.dicelab.whopuppy.data.AnimalRepository
import com.dicelab.whopuppy.data.entity.DogBreedAnalysis
import com.dicelab.whopuppy.data.response.toCommonResponse
import com.dicelab.whopuppy.util.SingleLiveEvent
import com.dicelab.whopuppy.util.handleHttpException
import com.dicelab.whopuppy.util.handleProgress
import com.dicelab.whopuppy.util.withThread
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
