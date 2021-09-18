package com.yunjaena.whopuppy.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yunjaena.whopuppy.base.viewmodel.ViewModelBase
import com.yunjaena.whopuppy.data.UserRepository
import com.yunjaena.whopuppy.data.entity.User
import com.yunjaena.whopuppy.data.response.toCommonResponse
import com.yunjaena.whopuppy.util.SingleLiveEvent
import com.yunjaena.whopuppy.util.toFile
import com.yunjaena.whopuppy.util.withThread
import io.reactivex.rxjava3.kotlin.addTo

class MyInfoViewModel(
    private val userRepository: UserRepository
) : ViewModelBase() {
    val userInfo: LiveData<User>
        get() = _userInfo
    private val _userInfo = MutableLiveData<User>()
    val logoutSuccess = SingleLiveEvent<Boolean>()
    val showErrorMessage = SingleLiveEvent<String>()

    fun getUserInfo() {
        userRepository.getUserInfo()
            .withThread()
            .subscribe({
                _userInfo.value = it
            }) {}.addTo(compositeDisposable)
    }

    fun updateUserProfile(uri: Uri) {
        userRepository.updateProfileImage(uri.toFile())
            .withThread()
            .subscribe({
                getUserInfo()
            }) {
                it.toCommonResponse()?.also { response ->
                    showErrorMessage.value = response.message
                }
            }.addTo(compositeDisposable)
    }

    fun logout() {
        userRepository.logout()
            .withThread()
            .subscribe({
                logoutSuccess.call()
            }) {}.addTo(compositeDisposable)
    }
}
