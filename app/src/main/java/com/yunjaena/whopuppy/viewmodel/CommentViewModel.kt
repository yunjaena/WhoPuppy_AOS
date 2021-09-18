package com.yunjaena.whopuppy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yunjaena.whopuppy.adapter.CommentContentView
import com.yunjaena.whopuppy.adapter.CommentViewType
import com.yunjaena.whopuppy.adapter.CommentWriteView
import com.yunjaena.whopuppy.base.viewmodel.ViewModelBase
import com.yunjaena.whopuppy.data.CommunityRepository
import com.yunjaena.whopuppy.data.UserRepository
import com.yunjaena.whopuppy.data.entity.CommentItem
import com.yunjaena.whopuppy.data.response.toCommonResponse
import com.yunjaena.whopuppy.util.SingleLiveEvent
import com.yunjaena.whopuppy.util.withThread
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.addTo

class CommentViewModel(
    private val communityRepository: CommunityRepository,
    private val userRepository: UserRepository
) : ViewModelBase() {
    val commentViews: LiveData<List<CommentViewType>>
        get() = _commentViews
    private val _commentViews = MutableLiveData<List<CommentViewType>>()
    val showErrorMessage = SingleLiveEvent<String>()
    val commentWriteSuccessEvent = SingleLiveEvent<Boolean>()

    fun getComments(articleId: Long, isRefresh: Boolean = false) {
        if (_commentViews.value != null && !isRefresh) return

        Single.zip(
            userRepository.getUserInfo(),
            communityRepository.getComments(articleId)
        ) { user, comment ->
            val commentArrayList = arrayListOf<CommentViewType>(
                CommentWriteView(
                    user.profileImageUrl,
                    user.nickname ?: "",
                    articleId
                )
            )
            commentArrayList.addAll(comment.map { CommentContentView(it) })
            commentArrayList
        }.withThread()
            .subscribe({
                _commentViews.value = it
            }) {

            }
    }

    fun addComment(articleId: Long, comment: String) {
        if (comment.isNullOrEmpty()) return

        communityRepository.writeComment(articleId, CommentItem(content = comment))
            .withThread()
            .subscribe({
                commentWriteSuccessEvent.call()
            }) {
                it.toCommonResponse()?.also { response ->
                    showErrorMessage.value = response.errorMessage
                }
            }.addTo(compositeDisposable)

    }
}
