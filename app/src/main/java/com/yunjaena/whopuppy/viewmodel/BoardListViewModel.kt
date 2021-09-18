package com.yunjaena.whopuppy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yunjaena.whopuppy.base.viewmodel.ViewModelBase
import com.yunjaena.whopuppy.data.Area
import com.yunjaena.whopuppy.data.CommunityRepository
import com.yunjaena.whopuppy.data.entity.Boards
import com.yunjaena.whopuppy.data.response.toCommonResponse
import com.yunjaena.whopuppy.util.SingleLiveEvent
import com.yunjaena.whopuppy.util.handleHttpException
import com.yunjaena.whopuppy.util.handleProgress
import com.yunjaena.whopuppy.util.withThread
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
