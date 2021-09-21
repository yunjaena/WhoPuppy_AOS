package com.dicelab.whopuppy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dicelab.whopuppy.base.viewmodel.ViewModelBase
import com.dicelab.whopuppy.data.Area
import com.dicelab.whopuppy.data.CommunityRepository
import com.dicelab.whopuppy.data.entity.Boards
import com.dicelab.whopuppy.data.response.toCommonResponse
import com.dicelab.whopuppy.util.SingleLiveEvent
import com.dicelab.whopuppy.util.handleHttpException
import com.dicelab.whopuppy.util.handleProgress
import com.dicelab.whopuppy.util.withThread
import io.reactivex.rxjava3.kotlin.addTo

class BoardListViewModel(
    private val communityRepository: CommunityRepository
) : ViewModelBase() {
    var selectArea = Area.ALL
    val boardList: LiveData<Boards>
        get() = _boardList
    private val _boardList = MutableLiveData<Boards>()
    val articleSearchEvent = SingleLiveEvent<ArticleSearchQuery>()
    private val articleSearch = ArticleSearchQuery()
    var boardSelectPosition = 0

    fun getBoards() {
        if (!_boardList.value.isNullOrEmpty()) return

        communityRepository.getBoards()
            .handleHttpException()
            .handleProgress(this)
            .withThread()
            .subscribe({
                _boardList.value = it
            }) {
                it.toCommonResponse()?.also { response ->

                }
            }.addTo(compositeDisposable)
    }

    fun refresh() {
        articleSearchEvent.value = articleSearch
    }

    fun updateSearchTitle(title: String?) {
        articleSearch.setArticleTitle(title)
        articleSearchEvent.value = articleSearch
    }

    fun updateSelectArea(selectArea: Area) {
        this.selectArea = selectArea
        articleSearch.setArea(selectArea)
        articleSearchEvent.value = articleSearch
    }
}
