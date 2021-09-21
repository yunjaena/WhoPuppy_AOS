package com.dicelab.whopuppy.data.local

import com.orhanobut.hawk.Hawk
import com.dicelab.whopuppy.constant.ACCESS_TOKEN
import com.dicelab.whopuppy.constant.REFRESH_TOKEN
import com.dicelab.whopuppy.data.UserDataSource
import com.dicelab.whopuppy.data.entity.AuthNumber
import com.dicelab.whopuppy.data.entity.User
import com.dicelab.whopuppy.data.response.CommonResponse
import com.dicelab.whopuppy.data.response.TokenResponse
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import java.io.File

class UserLocalDataSource : UserDataSource {
    override fun getToken(user: User): Single<TokenResponse> {
        return Single.create {

            val accessToken = Hawk.get(ACCESS_TOKEN, "")
            val refreshToken = Hawk.get(REFRESH_TOKEN, "")
            if (accessToken.isNullOrEmpty() || refreshToken.isNullOrEmpty()) {
                it.onError(Throwable("token is not saved"))
                return@create
            }
            val tokenResponse = TokenResponse()
            tokenResponse.apply {
                this.accessToken = accessToken
                this.refreshToken = refreshToken
            }
            it.onSuccess(tokenResponse)
        }
    }

    override fun login(user: User): Completable {
        return Completable.never()
    }

    override fun checkValidAccount(user: User): Single<CommonResponse> {
        return Single.never()
    }

    override fun checkValidNickName(user: User): Single<CommonResponse> {
        return Single.never()
    }

    override fun signUp(user: User): Single<CommonResponse> {
        return Single.never()
    }

    override fun phoneNumberValidCheckWithSms(authNumber: AuthNumber): Single<CommonResponse> {
        return Single.never()
    }

    override fun smsSecretKeyValidCheck(authNumber: AuthNumber): Single<CommonResponse> {
        return Single.never()
    }

    override fun refreshToken(): Single<TokenResponse> {
        return Single.never()
    }

    override fun saveToken(tokenResponse: TokenResponse): Completable {
        return Completable.create {
            Hawk.put(ACCESS_TOKEN, tokenResponse.accessToken)
            Hawk.put(REFRESH_TOKEN, tokenResponse.refreshToken)
            it.onComplete()
        }
    }

    override fun changePassword(user: User): Single<CommonResponse> {
        return Single.never()
    }

    override fun getUserInfo(): Single<User> {
        return Single.never()
    }

    override suspend fun getUserInfoCoroutine(): User {
        throw UnsupportedOperationException("getUserInfoCoroutine not supported")
    }

    override fun logout(): Completable {
        return Completable.create {
            if (it.isDisposed) return@create
            Hawk.delete(ACCESS_TOKEN)
            Hawk.delete(REFRESH_TOKEN)
            it.onComplete()
        }
    }

    override fun updateNickName(user: User): Single<CommonResponse> {
        return Single.never()
    }

    override fun updateProfileImage(file: File): Completable {
        return Completable.never()
    }
}
