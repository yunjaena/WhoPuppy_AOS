package com.dicelab.whopuppy.data.remote

import com.dicelab.whopuppy.api.AuthApi
import com.dicelab.whopuppy.api.NoAuthApi
import com.dicelab.whopuppy.data.UserDataSource
import com.dicelab.whopuppy.data.entity.AuthNumber
import com.dicelab.whopuppy.data.entity.User
import com.dicelab.whopuppy.data.response.CommonResponse
import com.dicelab.whopuppy.data.response.TokenResponse
import com.dicelab.whopuppy.util.FormDataUtil
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import java.io.File

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

    override fun updateProfileImage(file: File): Completable {
        return authApi.updateProfile(FormDataUtil.getImageBody("multipartFile", file))
    }
}
