package com.yunjaena.whopuppy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yunjaena.whopuppy.base.viewmodel.ViewModelBase
import com.yunjaena.whopuppy.data.CommunityRepository
import com.yunjaena.whopuppy.data.UserRepository
import com.yunjaena.whopuppy.util.withThread
import io.reactivex.rxjava3.kotlin.addTo

class CommentViewModel(
    private val communityRepository: CommunityRepository,
    private val userRepository: UserRepository
) : ViewModelBase() {
    val userProfile: LiveData<String>
        get() = _userProfile
    private val _userProfile = MutableLiveData<String>()

    private fun getUserProfile() {
        userRepository.getUserInfo()
            .withThread()
            .subscribe({

            }) {}.addTo(compositeDisposable)
    }
}
