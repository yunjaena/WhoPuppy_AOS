package com.dicelab.whopuppy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dicelab.whopuppy.base.viewmodel.ViewModelBase
import com.dicelab.whopuppy.constant.STATUS_OK
import com.dicelab.whopuppy.customview.ProgressStatus
import com.dicelab.whopuppy.data.UserRepository
import com.dicelab.whopuppy.data.entity.User
import com.dicelab.whopuppy.data.response.toCommonResponse
import com.dicelab.whopuppy.util.SingleLiveEvent
import com.dicelab.whopuppy.util.handleHttpException
import com.dicelab.whopuppy.util.handleProgress
import com.dicelab.whopuppy.util.withThread
import io.reactivex.rxjava3.kotlin.addTo

class MyInfoEditViewModel(
    private val userRepository: UserRepository
) : ViewModelBase() {
    val nickNameCheckErrorText: LiveData<String?>
        get() = _nickNameCheckErrorText
    val nickNameCheckStatus: LiveData<ProgressStatus>
        get() = _nickNameCheckStatus
    private var _nickNameCheckErrorText: MutableLiveData<String?> = MutableLiveData(null)
    private var _nickNameCheckStatus = MutableLiveData(ProgressStatus.STOP)
    private var checkSuccessNickName: String? = null
    val nickNameChangeSuccessEvent = SingleLiveEvent<Boolean?>()
    val nickNameChangeFailMessage = SingleLiveEvent<String>()

    fun checkNickNameValid(nickName: String) {
        _nickNameCheckStatus.value = ProgressStatus.LOADING
        _nickNameCheckErrorText.value = null

        userRepository.checkValidNickName(User(nickname = nickName))
            .handleHttpException()
            .withThread()
            .subscribe({
                if (it.httpStatus == STATUS_OK) {
                    _nickNameCheckStatus.value = ProgressStatus.SUCCESS
                    checkSuccessNickName = nickName
                } else
                    _nickNameCheckStatus.value = ProgressStatus.FAIL
            }) {
                it.toCommonResponse()?.also { response ->
                    _nickNameCheckErrorText.value = response.errorMessage
                }
                _nickNameCheckStatus.value = ProgressStatus.FAIL
            }.addTo(compositeDisposable)
    }

    fun changeNickName() {
        if (_nickNameCheckStatus.value != ProgressStatus.SUCCESS || checkSuccessNickName.isNullOrEmpty()) {
            return
        }

        userRepository.updateNickName(User(nickname = checkSuccessNickName))
            .handleHttpException()
            .withThread()
            .handleProgress(this)
            .subscribe({
                nickNameChangeSuccessEvent.call()
            }) {
                it.toCommonResponse()?.also { response ->
                    nickNameChangeFailMessage.value = response.errorMessage
                }
            }.addTo(compositeDisposable)

    }
}
