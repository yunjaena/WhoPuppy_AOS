package com.yunjaena.whopuppy.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.orhanobut.logger.Logger
import com.yunjaena.whopuppy.base.viewmodel.ViewModelBase
import com.yunjaena.whopuppy.data.Area
import com.yunjaena.whopuppy.data.CommunityRepository
import com.yunjaena.whopuppy.data.entity.ArticleItem
import com.yunjaena.whopuppy.data.response.toCommonResponse
import com.yunjaena.whopuppy.util.SingleLiveEvent
import com.yunjaena.whopuppy.util.handleHttpException
import com.yunjaena.whopuppy.util.handleProgress
import com.yunjaena.whopuppy.util.toFile
import com.yunjaena.whopuppy.util.withThread
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.addTo

class ArticleViewModel(
    private val communityRepository: CommunityRepository
) : ViewModelBase() {
    private var articleQuery: ArticleSearchQuery? = null
    var isEndOfPage: Boolean = false
    var isLoadingPage: Boolean = false
    private val article = arrayListOf<ArticleItem>()
    val articleList: LiveData<ArrayList<ArticleItem>>
        get() = _articleList
    private val _articleList = MutableLiveData<ArrayList<ArticleItem>>()

    val uploadArticleSuccessEvent = SingleLiveEvent<Int>()
    val uploadArticleFailEvent = SingleLiveEvent<String>()
    val imageUris: LiveData<ArrayList<Uri>>
        get() = _imageUris
    private val _imageUris = MutableLiveData<ArrayList<Uri>>(arrayListOf())

    val selectArea: LiveData<Area>
        get() = _selectArea
    private val _selectArea = MutableLiveData<Area>()

    val articleEmptyCheck = SingleLiveEvent<Article>()

    fun getArticles(articleQuery: ArticleSearchQuery) {
        if (articleQuery.isSameQuery(this.articleQuery)) return
        this.articleQuery = articleQuery
        article.clear()
        getArticleList()
    }

    fun getNextPage() {
        articleQuery?.nextPage()
        getArticleList()
    }

    fun refresh() {
        articleQuery?.initPage()
        article.clear()
        getArticleList()
    }

    private fun getArticleList() {
        val query = articleQuery
        if (query?.boardId == null) return
        Logger.d("NYJ query ${query.area}")
        communityRepository.getArticles(
            query.boardId!!,
            query.limit,
            query.page,
            query.area,
            query.title
        ).doFinally { isLoadingPage = false }
            .withThread()
            .subscribe({
                if (it.isNullOrEmpty()) {
                    isEndOfPage = true
                }
                article.addAll(it)
                _articleList.value = article
            }) {
                it.toCommonResponse()?.also { response ->

                }
            }.addTo(compositeDisposable)
    }

    fun addImageUri(uris: List<Uri>) {
        _imageUris.value?.addAll(uris)
        _imageUris.value = _imageUris.value
    }

    fun removeImageUri(uri: Uri) {
        _imageUris.value?.remove(uri)
        _imageUris.value = _imageUris.value
    }

    fun writeArticle(articleItem: ArticleItem) {
        if (!checkArticleValid(articleItem))
            return
        writeArticleToServer(articleItem)
    }

    private fun checkArticleValid(articleItem: ArticleItem): Boolean {
        val imageUriList = _imageUris.value!!

        if (articleItem.title.isNullOrEmpty()) {
            articleEmptyCheck.value = Title
            return false
        }

        if (selectArea.value == null || selectArea.value == Area.ALL) {
            articleEmptyCheck.value = Region
            return false
        }

        if (imageUriList.isNullOrEmpty()) {
            articleEmptyCheck.value = Image
            return false
        }

        if (articleItem.content.isNullOrEmpty()) {
            articleEmptyCheck.value = Content
            return false
        }
        return true
    }

    private fun writeArticleToServer(articleItem: ArticleItem) {
        val imageUriList = _imageUris.value!!

        val imageUploadRequest = imageUriList.map {
            when (it.toString().contains("http")) {
                true -> Single.just(it.toString())
                else -> communityRepository.uploadImage(it.toFile())
            }
        }

        Single.zip(imageUploadRequest) {
            it.map { url -> url as String }.toList()
        }.flatMap { imageUrls ->
            communityRepository.writeArticle(
                articleItem.copy(
                    images = imageUrls,
                    region = selectArea.value!!.areaName
                )
            )
        }.handleHttpException()
            .handleProgress(this)
            .withThread()
            .subscribe({
                uploadArticleSuccessEvent.value = it
            }) {
                it.toCommonResponse()?.also { response ->
                    uploadArticleFailEvent.value = response.errorMessage
                }
            }.addTo(compositeDisposable)
    }

    fun updateSelectArea(selectArea: Area) {
        _selectArea.value = selectArea
    }
}
