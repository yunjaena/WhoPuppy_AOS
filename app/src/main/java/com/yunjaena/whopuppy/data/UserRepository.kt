package com.yunjaena.whopuppy.data

import com.yunjaena.whopuppy.data.entity.AuthNumber
import com.yunjaena.whopuppy.data.entity.User
import com.yunjaena.whopuppy.data.response.CommonResponse
import com.yunjaena.whopuppy.data.response.TokenResponse
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

class UserRepository(
    private val userLocalDataSource: UserDataSource,
    private val userRemoteDataSource: UserDataSource
) : UserDataSource {
    override fun login(user: User): Completable {
        return userRemoteDataSource.getToken(user)
            .flatMapCompletable { userLocalDataSource.saveToken(it) }
    }

    override fun getToken(user: User): Single<TokenResponse> {
        return userLocalDataSource.getToken(user)
    }

    override fun checkValidAccount(user: User): Single<CommonResponse> {
        return userRemoteDataSource.checkValidAccount(user)
    }

    override fun checkValidNickName(user: User): Single<CommonResponse> {
        return userRemoteDataSource.checkValidNickName(user)
    }

    override fun signUp(user: User): Single<CommonResponse> {
        return userRemoteDataSource.signUp(user)
    }

    override fun phoneNumberValidCheckWithSms(authNumber: AuthNumber): Single<CommonResponse> {
        return userRemoteDataSource.phoneNumberValidCheckWithSms(authNumber)
    }

    override fun smsSecretKeyValidCheck(authNumber: AuthNumber): Single<CommonResponse> {
        return userRemoteDataSource.smsSecretKeyValidCheck(authNumber)
    }

    override fun refreshToken(): Single<TokenResponse> {
        return userRemoteDataSource.refreshToken()
            .flatMap {
                userLocalDataSource.saveToken(it)
                    .toSingle { it }
            }
    }

    override fun saveToken(tokenResponse: TokenResponse): Completable {
        return userLocalDataSource.saveToken(tokenResponse)
    }

    override fun changePassword(user: User): Single<CommonResponse> {
        return userRemoteDataSource.changePassword(user)
    }
}
