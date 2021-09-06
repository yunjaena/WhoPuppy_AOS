package com.yunjaena.whopuppy.di

import com.yunjaena.whopuppy.viewmodel.AbandonedDogSearchViewModel
import com.yunjaena.whopuppy.viewmodel.FindPasswordViewModel
import com.yunjaena.whopuppy.viewmodel.HomeViewModel
import com.yunjaena.whopuppy.viewmodel.LoginViewModel
import com.yunjaena.whopuppy.viewmodel.SignUpViewModel
import com.yunjaena.whopuppy.viewmodel.SplashViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { SplashViewModel(get()) }
    viewModel { SignUpViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { FindPasswordViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { AbandonedDogSearchViewModel(get()) }
}
