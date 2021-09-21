package com.dicelab.whopuppy.di

import com.dicelab.whopuppy.constant.AUTH
import com.dicelab.whopuppy.constant.AUTH_FLASK_SERVER
import com.dicelab.whopuppy.constant.NO_AUTH
import com.dicelab.whopuppy.constant.REFRESH_AUTH
import com.dicelab.whopuppy.data.AnimalRepository
import com.dicelab.whopuppy.data.ChatRepository
import com.dicelab.whopuppy.data.CommunityRepository
import com.dicelab.whopuppy.data.UserRepository
import com.dicelab.whopuppy.data.local.UserLocalDataSource
import com.dicelab.whopuppy.data.remote.AnimalRemoteDataSource
import com.dicelab.whopuppy.data.remote.ChatRemoteDataSource
import com.dicelab.whopuppy.data.remote.CommunityRemoteDataSource
import com.dicelab.whopuppy.data.remote.UserRemoteDataSource
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {
    single { UserLocalDataSource() }
    single { UserRemoteDataSource(get(named(REFRESH_AUTH)), get(named(NO_AUTH)), get(named(AUTH))) }
    single { UserRepository(get<UserLocalDataSource>(), get<UserRemoteDataSource>()) }
    single { AnimalRemoteDataSource(get(named(NO_AUTH)), get(named(AUTH_FLASK_SERVER))) }
    single { AnimalRepository(get<AnimalRemoteDataSource>()) }
    single { CommunityRemoteDataSource(get(named(AUTH))) }
    single { CommunityRepository(get<CommunityRemoteDataSource>()) }
    single { ChatRemoteDataSource(get(), get(named(AUTH))) }
    single { ChatRepository(get<ChatRemoteDataSource>()) }
}
