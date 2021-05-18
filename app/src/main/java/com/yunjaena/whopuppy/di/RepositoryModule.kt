package com.yunjaena.whopuppy.di

import com.yunjaena.whopuppy.constant.NO_AUTH
import com.yunjaena.whopuppy.constant.REFRESH_AUTH
import com.yunjaena.whopuppy.data.UserRepository
import com.yunjaena.whopuppy.data.local.UserLocalDataSource
import com.yunjaena.whopuppy.data.remote.UserRemoteDataSource
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {
    single { UserLocalDataSource() }
    single { UserRemoteDataSource(get(named(REFRESH_AUTH)), get(named(NO_AUTH))) }
    single { UserRepository(get<UserLocalDataSource>(), get<UserRemoteDataSource>()) }
}
