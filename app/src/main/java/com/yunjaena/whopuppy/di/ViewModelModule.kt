package com.yunjaena.whopuppy.di

import com.yunjaena.whopuppy.viewmodel.SignUpViewModel
import com.yunjaena.whopuppy.viewmodel.SplashViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { SplashViewModel() }
    viewModel { SignUpViewModel(get()) }
}
