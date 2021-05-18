package com.yunjaena.whopuppy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yunjaena.whopuppy.R
import com.yunjaena.whopuppy.base.viewmodel.ViewModelBase
import com.yunjaena.whopuppy.constant.STATUS_OK
import com.yunjaena.whopuppy.customview.ProgressStatus
import com.yunjaena.whopuppy.data.UserRepository
import com.yunjaena.whopuppy.data.entity.AuthNumber
import com.yunjaena.whopuppy.data.entity.User
import com.yunjaena.whopuppy.data.response.errorTextPrettyFormat
import com.yunjaena.whopuppy.data.response.toCommonResponse
import com.yunjaena.whopuppy.util.SingleLiveEvent
import com.yunjaena.whopuppy.util.handleHttpException
import com.yunjaena.whopuppy.util.handleProgress
import com.yunjaena.whopuppy.util.isPasswordValid
import com.yunjaena.whopuppy.util.withThread

class SignUpViewModel(
    private val userRepository: UserRepository
) : ViewModelBase() {
    val accountCheckErrorText: LiveData<String?>
        get() = _accountCheckErrorText
    val accountCheckStatus: LiveData<ProgressStatus>
        get() = _accountCheckStatus
    private var _accountCheckErrorText: MutableLiveData<String?> = MutableLiveData(null)
    private var _accountCheckStatus = MutableLiveData(ProgressStatus.STOP)

    val nickNameCheckErrorText: LiveData<String?>
        get() = _nickNameCheckErrorText
    val nickNameCheckStatus: LiveData<ProgressStatus>
        get() = _nickNameCheckStatus
    private var _nickNameCheckErrorText: MutableLiveData<String?> = MutableLiveData(null)
    private var _nickNameCheckStatus = MutableLiveData(ProgressStatus.STOP)

    val passwordCheckErrorText: LiveData<Int?>
        get() = _passwordCheckErrorText
    val passwordCheckStatus: LiveData<ProgressStatus>
        get() = _passwordCheckStatus
    private var _passwordCheckErrorText: MutableLiveData<Int?> = MutableLiveData(null)
    private var _passwordCheckStatus = MutableLiveData(ProgressStatus.STOP)

    val phoneNumberStatusMessage = SingleLiveEvent<@androidx.annotation.StringRes Int?>()
    val phoneNumberCheckErrorText: LiveData<String?>
        get() = _phoneNumberErrorText
    val phoneNumberStatus: LiveData<ProgressStatus>
        get() = _phoneNumberStatus
    private var _phoneNumberErrorText: MutableLiveData<String?> = MutableLiveData(null)
    private var _phoneNumberStatus = MutableLiveData(ProgressStatus.STOP)

    val secretCodeCheckErrorText: LiveData<String?>
        get() = _secretCodeErrorText
    val secretCodeStatus: LiveData<ProgressStatus>
        get() = _secretCodeStatus
    val secretCodeStatusMessage = SingleLiveEvent<@androidx.annotation.StringRes Int?>()
    private var _secretCodeErrorText: MutableLiveData<String?> = MutableLiveData(null)
    private var _secretCodeStatus = MutableLiveData(ProgressStatus.STOP)

    val signUpErrorMessage = SingleLiveEvent<String>()
    val signUpResult = SingleLiveEvent<Boolean>()

    fun checkAccountValid(account: String) {
        _accountCheckStatus.value = ProgressStatus.LOADING
        _accountCheckErrorText.value = null

        userRepository.checkValidAccount(User(account = account))
            .handleHttpException()
            .withThread()
            .subscribe({
                if (it.httpStatus == STATUS_OK)
                    _accountCheckStatus.value = ProgressStatus.SUCCESS
                else
                    _accountCheckStatus.value = ProgressStatus.FAIL
            }) {
                it.toCommonResponse()?.also { response ->
                    _accountCheckErrorText.value = response.errorMessage
                }
                _accountCheckStatus.value = ProgressStatus.FAIL
            }
    }

    fun checkNickNameValid(nickName: String) {
        _nickNameCheckStatus.value = ProgressStatus.LOADING
        _nickNameCheckErrorText.value = null

        userRepository.checkValidNickName(User(nickname = nickName))
            .handleHttpException()
            .withThread()
            .subscribe({
                if (it.httpStatus == STATUS_OK)
                    _nickNameCheckStatus.value = ProgressStatus.SUCCESS
                else
                    _nickNameCheckStatus.value = ProgressStatus.FAIL
            }) {
                it.toCommonResponse()?.also { response ->
                    _nickNameCheckErrorText.value = response.errorMessage
                }
                _nickNameCheckStatus.value = ProgressStatus.FAIL
            }
    }

    fun checkPasswordValid(password: String) {
        _passwordCheckStatus.value = ProgressStatus.LOADING
        _passwordCheckErrorText.value = null

        if (!password.isPasswordValid()) {
            _passwordCheckErrorText.value = R.string.password_input_error
            _passwordCheckStatus.value = ProgressStatus.FAIL
        } else {
            _passwordCheckErrorText.value = null
            _passwordCheckStatus.value = ProgressStatus.SUCCESS
        }
    }

    fun phoneNumberValidCheckWithSms(phoneNumber: String, code: String) {
        _phoneNumberStatus.value = ProgressStatus.LOADING
        _phoneNumberErrorText.value = null

        userRepository.phoneNumberValidCheckWithSms(
            AuthNumber(
                phoneNumber = phoneNumber,
                flag = 0,
                code = code
            )
        ).handleHttpException()
            .handleProgress(this)
            .withThread()
            .subscribe({
                if (it.httpStatus == STATUS_OK) {
                    phoneNumberStatusMessage.value = R.string.secret_key_send_success
                    _phoneNumberStatus.value = ProgressStatus.SUCCESS
                } else
                    _phoneNumberStatus.value = ProgressStatus.FAIL
            }) {
                it.toCommonResponse()?.also { response ->
                    _phoneNumberErrorText.value = response.errorMessage
                }
                _phoneNumberStatus.value = ProgressStatus.FAIL
            }
    }

    fun checkSecretCode(phoneNumber: String, code: String) {
        _secretCodeStatus.value = ProgressStatus.LOADING
        _secretCodeErrorText.value = null

        userRepository.smsSecretKeyValidCheck(
            AuthNumber(
                phoneNumber = phoneNumber,
                secret = code,
                flag = 0,
            )
        ).handleHttpException()
            .handleProgress(this)
            .withThread()
            .subscribe({
                if (it.httpStatus == STATUS_OK) {
                    _secretCodeStatus.value = ProgressStatus.SUCCESS
                    secretCodeStatusMessage.value = R.string.secret_code_verify_success
                } else
                    _secretCodeStatus.value = ProgressStatus.FAIL
            }) {
                it.toCommonResponse()?.also { response ->
                    _secretCodeErrorText.value = response.errorMessage
                }
                _secretCodeStatus.value = ProgressStatus.FAIL
            }
    }

    fun signUp(user: User) {
        userRepository.signUp(user)
            .handleHttpException()
            .handleProgress(this)
            .withThread()
            .subscribe({
                signUpResult.value = it.httpStatus == STATUS_OK
            }) {
                it.toCommonResponse()?.also { response ->
                    signUpErrorMessage.value = response.errorMessage?.errorTextPrettyFormat()
                }
            }
    }

    companion object {
        const val PASSWORD_MIN_LENGTH = 8
    }
}
