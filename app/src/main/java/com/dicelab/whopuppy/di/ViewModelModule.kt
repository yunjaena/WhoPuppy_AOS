package com.dicelab.whopuppy.di

import com.dicelab.whopuppy.viewmodel.AbandonedDogDetailViewModel
import com.dicelab.whopuppy.viewmodel.AbandonedDogSearchViewModel
import com.dicelab.whopuppy.viewmodel.ArticleViewModel
import com.dicelab.whopuppy.viewmodel.BoardListViewModel
import com.dicelab.whopuppy.viewmodel.BreedCheckViewModel
import com.dicelab.whopuppy.viewmodel.ChatListViewModel
import com.dicelab.whopuppy.viewmodel.ChatRoomViewModel
import com.dicelab.whopuppy.viewmodel.CommentViewModel
import com.dicelab.whopuppy.viewmodel.DogImageUploadViewModel
import com.dicelab.whopuppy.viewmodel.FindPasswordViewModel
import com.dicelab.whopuppy.viewmodel.HomeViewModel
import com.dicelab.whopuppy.viewmodel.LoginViewModel
import com.dicelab.whopuppy.viewmodel.MyInfoEditViewModel
import com.dicelab.whopuppy.viewmodel.MyInfoViewModel
import com.dicelab.whopuppy.viewmodel.SignUpViewModel
import com.dicelab.whopuppy.viewmodel.SplashViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { SplashViewModel(get()) }
    viewModel { SignUpViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { FindPasswordViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { AbandonedDogSearchViewModel(get()) }
    viewModel { AbandonedDogDetailViewModel(get()) }
    viewModel { MyInfoViewModel(get()) }
    viewModel { MyInfoEditViewModel(get()) }
    viewModel { DogImageUploadViewModel(get()) }
    viewModel { BreedCheckViewModel(get()) }
    viewModel { BoardListViewModel(get()) }
    viewModel { CommentViewModel(get(), get()) }
    viewModel { ArticleViewModel(get(), get()) }
    viewModel { ChatListViewModel(get(), get()) }
    viewModel { ChatRoomViewModel(get(), get(), get()) }
}
