package com.yunjaena.whopuppy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yunjaena.whopuppy.base.viewmodel.ViewModelBase
import com.yunjaena.whopuppy.data.AnimalRepository
import com.yunjaena.whopuppy.data.entity.AbandonedDogItem
import com.yunjaena.whopuppy.data.response.toCommonResponse
import com.yunjaena.whopuppy.util.SingleLiveEvent
import com.yunjaena.whopuppy.util.withThread
import io.reactivex.rxjava3.kotlin.addTo

class HomeViewModel(
    private val animalRepository: AnimalRepository
) : ViewModelBase() {
    val abandonedAnimalList: LiveData<ArrayList<AbandonedDogItem>>
        get() = _abandonedAnimalList
    private val _abandonedAnimalList = MutableLiveData<ArrayList<AbandonedDogItem>>()
    val abandonedDogItemFetchFailMessage = SingleLiveEvent<String>()

    init {
        getAnimalList(ANIMAL_LIST_LIMIT_SIZE, ANIMAL_LIST_PAGE)
    }

    fun getAnimalList(limit: Int, page: Int) {
        animalRepository.getAnimalList(
            kindCd = null,
            limit = limit,
            noticeNo = null,
            page = page,
            sexCd = null
        ).withThread()
            .subscribe({
                _abandonedAnimalList.value = it
            }) {
                it.toCommonResponse()?.also { response ->
                    abandonedDogItemFetchFailMessage.value = response.errorMessage
                }
            }.addTo(compositeDisposable)
    }

    companion object {
        const val ANIMAL_LIST_LIMIT_SIZE = 10
        const val ANIMAL_LIST_PAGE = 1
    }
}
