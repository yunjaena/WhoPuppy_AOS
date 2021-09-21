package com.dicelab.whopuppy.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dicelab.whopuppy.base.viewmodel.ViewModelBase
import com.dicelab.whopuppy.data.UserRepository
import com.dicelab.whopuppy.data.entity.User
import com.dicelab.whopuppy.data.response.toCommonResponse
import com.dicelab.whopuppy.util.SingleLiveEvent
import com.dicelab.whopuppy.util.toFile
import com.dicelab.whopuppy.util.withThread
import io.reactivex.rxjava3.kotlin.addTo

class MyInfoViewModel(
    private val userRepository: UserRepository
) : ViewModelBase() {
    val userInfo: LiveData<User>
        get() = _userInfo
    private val _userInfo = MutableLiveData<User>()
    val logoutSuccess = SingleLiveEvent<Boolean>()
    val showErrorMessage = SingleLiveEvent<String>()
    val fetchFailEvent = SingleLiveEvent<Boolean>()

    fun getUserInfo() {
        userRepository.getUserInfo()
            .withThread()
            .subscribe({
                _userInfo.value = it
            }) {
                it.toCommonResponse()?.also { response ->
                    showErrorMessage.value = response.message
                }
                fetchFailEvent.call()
            }.addTo(compositeDisposable)
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
