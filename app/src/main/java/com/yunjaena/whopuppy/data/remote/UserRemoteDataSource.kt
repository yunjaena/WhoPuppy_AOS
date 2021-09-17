package com.yunjaena.whopuppy.data.remote

import com.yunjaena.whopuppy.api.AuthApi
import com.yunjaena.whopuppy.api.NoAuthApi
import com.yunjaena.whopuppy.data.UserDataSource
import com.yunjaena.whopuppy.data.entity.AuthNumber
import com.yunjaena.whopuppy.data.entity.User
import com.yunjaena.whopuppy.data.response.CommonResponse
import com.yunjaena.whopuppy.data.response.TokenResponse
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

class UserRemoteDataSource(
    private val refreshTokenApi: AuthApi,
    private val noAuthApi: NoAuthApi,
    private val authApi: AuthApi
) : UserDataSource {

    override fun getToken(user: User): Single<TokenResponse> {
        return noAuthApi.login(user)
    }

    override fun login(user: User): Completable {
        return Completable.never()
    }

    override fun checkValidAccount(user: User): Single<CommonResponse> {
        return noAuthApi.checkValidAccount(user)
    }

    override fun checkValidNickName(user: User): Single<CommonResponse> {
        return noAuthApi.checkValidNickName(user)
    }

    override fun signUp(user: User): Single<CommonResponse> {
        return noAuthApi.signUp(user)
    }

    override fun phoneNumberValidCheckWithSms(authNumber: AuthNumber): Single<CommonResponse> {
        return noAuthApi.sendSms(authNumber)
    }

    override fun smsSecretKeyValidCheck(authNumber: AuthNumber): Single<CommonResponse> {
        return noAuthApi.checkSmsSecretValid(authNumber)
    }

    override fun refreshToken(): Single<TokenResponse> {
        return refreshTokenApi.refreshToken()
    }

    override fun saveToken(tokenResponse: TokenResponse): Completable {
        return Completable.never()
    }

    override fun changePassword(user: User): Single<CommonResponse> {
        return noAuthApi.changePassword(user)
    }

    override fun getUserInfo(): Single<User> {
        return authApi.getUserInfo()
    }

    override suspend fun getUserInfoCoroutine(): User {
        return authApi.getUserInfoCoroutine()
    }

    override fun logout(): Completable {
        return Completable.never()
    }

    override fun updateNickName(user: User): Single<CommonResponse> {
        return authApi.updateNickName(user)
    }
}
