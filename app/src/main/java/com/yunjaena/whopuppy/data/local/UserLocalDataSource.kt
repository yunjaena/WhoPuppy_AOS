package com.yunjaena.whopuppy.data.local

import com.orhanobut.hawk.Hawk
import com.yunjaena.whopuppy.constant.ACCESS_TOKEN
import com.yunjaena.whopuppy.constant.REFRESH_TOKEN
import com.yunjaena.whopuppy.data.UserDataSource
import com.yunjaena.whopuppy.data.entity.AuthNumber
import com.yunjaena.whopuppy.data.entity.User
import com.yunjaena.whopuppy.data.response.CommonResponse
import com.yunjaena.whopuppy.data.response.TokenResponse
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

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
}
