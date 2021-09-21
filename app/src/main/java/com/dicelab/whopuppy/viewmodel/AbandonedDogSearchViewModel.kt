package com.dicelab.whopuppy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dicelab.whopuppy.base.viewmodel.ViewModelBase
import com.dicelab.whopuppy.data.AnimalRepository
import com.dicelab.whopuppy.data.entity.AbandonedAnimalItem
import com.dicelab.whopuppy.data.response.toCommonResponse
import com.dicelab.whopuppy.util.SingleLiveEvent
import com.dicelab.whopuppy.util.withThread
import io.reactivex.rxjava3.kotlin.addTo

class AbandonedDogSearchViewModel(
    private val animalRepository: AnimalRepository
) : ViewModelBase() {
    val animalSearchQuery: AnimalSearchQuery = AnimalSearchQuery()
    var isEndOfPage: Boolean = false
    var isLoadingPage: Boolean = false
    val abandonedAnimalList: LiveData<ArrayList<AbandonedAnimalItem>>
        get() = _abandonedAnimalList
    private val _abandonedAnimalList = MutableLiveData<ArrayList<AbandonedAnimalItem>>()
    val abandonedDogItemFetchFailMessage = SingleLiveEvent<String>()
    private val animals = ArrayList<AbandonedAnimalItem>()

    init {
        getAnimalList()
    }

    fun setKind(kind: String?) {
        if (isSameKindWithQuery(kind)) return
        clearList()
        animalSearchQuery.initPage()
        animalSearchQuery.setKind(kind)
        getAnimalList()
    }

    private fun isSameKindWithQuery(kind: String?): Boolean {
        return animalSearchQuery.kindCd == kind
    }

    fun setArea(area: String?) {
        if (isSameAreaWithQuery(area)) return
        clearList()
        animalSearchQuery.initPage()
        animalSearchQuery.setArea(area)
        getAnimalList()
    }

    private fun isSameAreaWithQuery(area: String?): Boolean {
        return animalSearchQuery.noticeNo == area
    }

    fun setSex(sex: Sex) {
        if (isSameSexWithQuery(sex)) return
        clearList()
        animalSearchQuery.initPage()
        animalSearchQuery.setSex(sex)
        getAnimalList()
    }

    private fun isSameSexWithQuery(sex: Sex): Boolean {
        return animalSearchQuery.sexCd == sex
    }

    fun getNextPage() {
        animalSearchQuery.nextPage()
        getAnimalList()
    }

    private fun clearList() {
        isEndOfPage = false
        animals.clear()
    }

    private fun getAnimalList() {
        isLoadingPage = true
        animalRepository.getAnimalList(
            kindCd = animalSearchQuery.kindCd,
            limit = animalSearchQuery.limit,
            noticeNo = animalSearchQuery.noticeNo,
            page = animalSearchQuery.page,
            sexCd = animalSearchQuery.sexCd.type
        ).doFinally { isLoadingPage = false }
            .withThread()
            .subscribe({
                if (it.isNullOrEmpty()) {
                    isEndOfPage = true
                }
                animals.addAll(it)
                _abandonedAnimalList.value = animals
            }) {
                it.toCommonResponse()?.also { response ->
                    abandonedDogItemFetchFailMessage.value = response.errorMessage
                }
            }.addTo(compositeDisposable)

    }
}
