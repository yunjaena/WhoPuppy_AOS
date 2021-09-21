package com.dicelab.whopuppy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dicelab.whopuppy.adapter.CommentContentView
import com.dicelab.whopuppy.adapter.CommentViewType
import com.dicelab.whopuppy.adapter.CommentWriteView
import com.dicelab.whopuppy.base.viewmodel.ViewModelBase
import com.dicelab.whopuppy.data.CommunityRepository
import com.dicelab.whopuppy.data.UserRepository
import com.dicelab.whopuppy.data.entity.CommentItem
import com.dicelab.whopuppy.data.response.toCommonResponse
import com.dicelab.whopuppy.util.SingleLiveEvent
import com.dicelab.whopuppy.util.withThread
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
    val fetchFailEvent = SingleLiveEvent<Boolean>()

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
                it.toCommonResponse()?.also { response ->
                    showErrorMessage.value = response.errorMessage
                }
                fetchFailEvent.call()
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
