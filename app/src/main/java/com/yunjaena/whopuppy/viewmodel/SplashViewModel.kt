package com.yunjaena.whopuppy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yunjaena.whopuppy.base.viewmodel.ViewModelBase
import com.yunjaena.whopuppy.data.UserRepository
import com.yunjaena.whopuppy.util.withThread

class SplashViewModel(
    private val userRepository: UserRepository
) : ViewModelBase() {
    val isLoginSuccess: LiveData<Boolean>
        get() = _isLoginSuccess
    private var _isLoginSuccess = MutableLiveData<Boolean>()

    fun checkAutoLogin() {
        userRepository.refreshToken()
            .withThread()
            .subscribe({
                _isLoginSuccess.value = true
            }) {
                _isLoginSuccess.value = false
            }
    }
}
