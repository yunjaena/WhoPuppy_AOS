package com.dicelab.whopuppy.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.orhanobut.logger.Logger
import com.dicelab.whopuppy.base.viewmodel.ViewModelBase
import com.dicelab.whopuppy.data.Area
import com.dicelab.whopuppy.data.CommunityRepository
import com.dicelab.whopuppy.data.UserRepository
import com.dicelab.whopuppy.data.entity.ArticleItem
import com.dicelab.whopuppy.data.response.toCommonResponse
import com.dicelab.whopuppy.util.SingleLiveEvent
import com.dicelab.whopuppy.util.handleHttpException
import com.dicelab.whopuppy.util.handleProgress
import com.dicelab.whopuppy.util.toFile
import com.dicelab.whopuppy.util.withThread
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.addTo

class ArticleViewModel(
    private val communityRepository: CommunityRepository,
    private val userRepository: UserRepository,
) : ViewModelBase() {
    private var articleQuery: ArticleSearchQuery? = null
    var isEndOfPage: Boolean = false
    var isLoadingPage: Boolean = false
    private val article = arrayListOf<ArticleItem>()
    val articleList: LiveData<ArrayList<ArticleItem>>
        get() = _articleList
    private val _articleList = MutableLiveData<ArrayList<ArticleItem>>()

    val uploadArticleSuccessEvent = SingleLiveEvent<Long>()
    val imageUris: LiveData<ArrayList<Uri>>
        get() = _imageUris
    private val _imageUris = MutableLiveData<ArrayList<Uri>>(arrayListOf())

    val selectArea: LiveData<Area>
        get() = _selectArea
    private val _selectArea = MutableLiveData<Area>()

    val articleEmptyCheck = SingleLiveEvent<Article>()

    val articleDetailItem: LiveData<ArticleItem>
        get() = _articleDetailItem
    private val _articleDetailItem = MutableLiveData<ArticleItem>()
    val articleFetchEvent = SingleLiveEvent<ArticleItem>()
    val showErrorMessage = SingleLiveEvent<String>()
    val fetchFailEvent = SingleLiveEvent<Boolean>()
    val deleteArticleSuccessEvent = SingleLiveEvent<Boolean>()
    val updateArticleSuccessEvent = SingleLiveEvent<Boolean>()
    var isArticleOwner = false


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

    fun refreshArticleList() {
        articleQuery?.initPage()
        article.clear()
        getArticleList()
    }

    private fun getArticleList() {
        val query = articleQuery
        if (query?.boardId == null) return
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
                    showErrorMessage.value = response.errorMessage
                }
                fetchFailEvent.call()
            }.addTo(compositeDisposable)
    }

    fun getArticle(articleId: Long) {
        if (articleDetailItem.value != null) return
        getArticleFromServer(articleId)
    }

    fun refreshArticle(articleId: Long) {
        getArticleFromServer(articleId)
    }

    private fun getArticleFromServer(articleId: Long) {
        communityRepository.getArticle(articleId)
            .handleHttpException()
            .handleProgress(this)
            .withThread()
            .subscribe({
                _articleDetailItem.value = it
                articleFetchEvent.value = it
            }) {
                it.toCommonResponse()?.also { response ->
                    showErrorMessage.value = response.errorMessage
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

    fun editArticle(articleId: Long, articleItem: ArticleItem) {
        if (!checkArticleValid(articleItem))
            return
        updateArticleToServer(articleId, articleItem)
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

    fun checkUserWriteArticle(articleItem: ArticleItem) {
        userRepository.getUserInfo()
            .handleHttpException()
            .handleProgress(this)
            .withThread()
            .subscribe({
                isArticleOwner = (articleItem.userId == it.id)
            }) {

            }.addTo(compositeDisposable)

    }

    fun deleteArticle(articleId: Long) {
        communityRepository.deleteArticle(articleId)
            .handleHttpException()
            .handleProgress(this)
            .withThread()
            .subscribe({
                deleteArticleSuccessEvent.call()
            }) {
                it.toCommonResponse()?.also { response ->
                    showErrorMessage.value = response.errorMessage
                }
            }.addTo(compositeDisposable)
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
                    showErrorMessage.value = response.errorMessage
                }
            }.addTo(compositeDisposable)
    }

    private fun updateArticleToServer(articleId: Long, articleItem: ArticleItem) {
        val imageUriList = _imageUris.value!!

        val imageUploadRequest = imageUriList.map {
            when (it.toString().contains("http")) {
                true -> Single.just(it.toString())
                else -> communityRepository.uploadImage(it.toFile())
            }
        }

        Single.zip(imageUploadRequest) {
            it.map { url -> url as String }.toList()
        }.flatMapCompletable { imageUrls ->
            communityRepository.updateArticle(
                articleItem.copy(
                    id = articleId,
                    images = imageUrls,
                    region = selectArea.value!!.areaName
                )
            )
        }.handleHttpException()
            .handleProgress(this)
            .withThread()
            .subscribe({
                updateArticleSuccessEvent.call()
            }) {
                it.toCommonResponse()?.also { response ->
                    showErrorMessage.value = response.errorMessage
                }
            }.addTo(compositeDisposable)
    }

    fun updateSelectArea(selectArea: Area) {
        _selectArea.value = selectArea
    }
}
