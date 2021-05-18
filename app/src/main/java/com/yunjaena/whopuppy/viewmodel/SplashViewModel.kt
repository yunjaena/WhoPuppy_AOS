package com.yunjaena.whopuppy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yunjaena.whopuppy.base.viewmodel.ViewModelBase

class SplashViewModel : ViewModelBase() {
    val isLoginSuccess: LiveData<Boolean>
        get() = _isLoginSuccess
    private var _isLoginSuccess = MutableLiveData<Boolean>()

    fun checkAutoLogin() {
        // TODO -> 자동 로그인 로직 구현
        _isLoginSuccess.value = false
    }
}
