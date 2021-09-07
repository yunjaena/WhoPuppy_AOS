package com.yunjaena.whopuppy.data

import com.yunjaena.whopuppy.data.entity.AuthNumber
import com.yunjaena.whopuppy.data.entity.User
import com.yunjaena.whopuppy.data.response.CommonResponse
import com.yunjaena.whopuppy.data.response.TokenResponse
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface UserDataSource {
    fun getToken(user: User): Single<TokenResponse>

    fun login(user: User): Completable

    fun checkValidAccount(user: User): Single<CommonResponse>

    fun checkValidNickName(user: User): Single<CommonResponse>

    fun signUp(user: User): Single<CommonResponse>

    fun phoneNumberValidCheckWithSms(authNumber: AuthNumber): Single<CommonResponse>

    fun smsSecretKeyValidCheck(authNumber: AuthNumber): Single<CommonResponse>

    fun refreshToken(): Single<TokenResponse>

    fun saveToken(tokenResponse: TokenResponse): Completable

    fun changePassword(user: User): Single<CommonResponse>

    fun getUserInfo(): Single<User>

    fun logout(): Completable
}
