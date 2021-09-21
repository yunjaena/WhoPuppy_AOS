package com.dicelab.whopuppy.viewmodel

import androidx.annotation.StringRes
import com.dicelab.whopuppy.R
import com.dicelab.whopuppy.base.viewmodel.ViewModelBase
import com.dicelab.whopuppy.data.UserRepository
import com.dicelab.whopuppy.data.entity.AuthNumber
import com.dicelab.whopuppy.data.entity.User
import com.dicelab.whopuppy.data.response.toCommonResponse
import com.dicelab.whopuppy.util.SingleLiveEvent
import com.dicelab.whopuppy.util.handleHttpException
import com.dicelab.whopuppy.util.handleProgress
import com.dicelab.whopuppy.util.isPasswordValid
import com.dicelab.whopuppy.util.toSha256
import com.dicelab.whopuppy.util.withThread
import io.reactivex.rxjava3.kotlin.addTo

class FindPasswordViewModel(
    private val userRepository: UserRepository
) : ViewModelBase() {
    val isSecretCodeCheckSuccess = SingleLiveEvent<Boolean>()
    val alertMessage = SingleLiveEvent<String>()
    val alertMessageRes = SingleLiveEvent<@StringRes Int>()
    val findPasswordSuccessEvent = SingleLiveEvent<Boolean>()

    fun phoneNumberValidCheckWithSms(phoneNumber: String, code: String) {
        val authNumber =
            AuthNumber(phoneNumber = phoneNumber, flag = FIND_PASSWORD_FLAG, code = code)

        userRepository.phoneNumberValidCheckWithSms(authNumber)
            .handleHttpException()
            .handleProgress(this)
            .withThread()
            .subscribe({
                alertMessage.value = it.message
            }) {
                it.toCommonResponse()?.also { response ->
                    alertMessage.value = response.errorMessage
                }
            }.addTo(compositeDisposable)
    }

    fun checkSecretCode(phoneNumber: String, secretCode: String) {
        val authNumber =
            AuthNumber(phoneNumber = phoneNumber, flag = FIND_PASSWORD_FLAG, secret = secretCode)

        userRepository.smsSecretKeyValidCheck(authNumber)
            .handleHttpException()
            .handleProgress(this)
            .withThread()
            .subscribe({
                alertMessage.value = it.message
                isSecretCodeCheckSuccess.value = true
            }) {
                it.toCommonResponse()?.also { response ->
                    alertMessage.value = response.errorMessage
                    isSecretCodeCheckSuccess.value = false
                }
            }.addTo(compositeDisposable)
    }

    fun changePassword(
        account: String,
        phoneNumber: String,
        password: String,
        checkPassword: String,
        secretCode: String
    ) {

        if (password != checkPassword) {
            alertMessageRes.value = R.string.different_password
            return
        }

        if (!password.isPasswordValid()) {
            alertMessageRes.value = R.string.password_input_error
            return
        }

        val user = User(
            account = account,
            phoneNumber = phoneNumber,
            password = password.toSha256(),
            secret = secretCode
        )

        userRepository.changePassword(user)
            .handleHttpException()
            .handleProgress(this)
            .withThread()
            .subscribe({
                findPasswordSuccessEvent.call()
            }) {
                it.toCommonResponse()?.also { response ->
                    alertMessage.value = response.errorMessage
                    findPasswordSuccessEvent.call()
                }
            }.addTo(compositeDisposable)
    }

    companion object {
        const val FIND_PASSWORD_FLAG = 1
    }
}
