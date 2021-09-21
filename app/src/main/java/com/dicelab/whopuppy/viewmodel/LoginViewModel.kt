package com.dicelab.whopuppy.viewmodel

import com.dicelab.whopuppy.base.viewmodel.ViewModelBase
import com.dicelab.whopuppy.data.UserRepository
import com.dicelab.whopuppy.data.entity.User
import com.dicelab.whopuppy.data.response.toCommonResponse
import com.dicelab.whopuppy.util.SingleLiveEvent
import com.dicelab.whopuppy.util.handleProgress
import com.dicelab.whopuppy.util.toSha256
import com.dicelab.whopuppy.util.withThread
import io.reactivex.rxjava3.kotlin.addTo

class LoginViewModel(
    private val userRepository: UserRepository
) : ViewModelBase() {
    val loginSuccessEvent = SingleLiveEvent<Boolean>()
    val loginFailMessage = SingleLiveEvent<String?>()

    fun login(id: String, password: String) {
        val user = User(account = id, password = password.toSha256())
        userRepository.login(user)
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
