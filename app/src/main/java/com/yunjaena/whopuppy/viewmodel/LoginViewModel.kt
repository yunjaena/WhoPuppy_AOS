package com.yunjaena.whopuppy.viewmodel

import com.yunjaena.whopuppy.base.viewmodel.ViewModelBase
import com.yunjaena.whopuppy.data.UserRepository
import com.yunjaena.whopuppy.data.entity.User
import com.yunjaena.whopuppy.data.response.toCommonResponse
import com.yunjaena.whopuppy.util.SingleLiveEvent
import com.yunjaena.whopuppy.util.handleHttpException
import com.yunjaena.whopuppy.util.handleProgress
import com.yunjaena.whopuppy.util.toSha256
import com.yunjaena.whopuppy.util.withThread
import io.reactivex.rxjava3.kotlin.addTo

class LoginViewModel(
    private val userRepository: UserRepository
) : ViewModelBase() {
    val loginSuccessEvent = SingleLiveEvent<Boolean>()
    val loginFailMessage = SingleLiveEvent<String?>()

    fun login(id: String, password: String) {
        val user = User(account = id, password = password.toSha256())
        userRepository.login(user)
            .handleHttpException()
            .handleProgress(this)
            .withThread()
            .subscribe({
                loginSuccessEvent.call()
            }) {
                it.toCommonResponse()?.also { response ->
                    loginFailMessage.value = response.errorMessage
                }
            }.addTo(compositeDisposable)
    }
}
