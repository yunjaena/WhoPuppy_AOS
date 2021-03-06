package com.yunjaena.whopuppy.api

import com.yunjaena.whopuppy.data.entity.AuthNumber
import com.yunjaena.whopuppy.data.entity.User
import com.yunjaena.whopuppy.data.response.CommonResponse
import com.yunjaena.whopuppy.data.response.TokenResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface NoAuthApi {
    @POST("/user/account")
    fun checkValidAccount(@Body user: User): Single<CommonResponse>

    @POST("/user/login")
    fun login(@Body user: User): Single<TokenResponse>

    @POST("/user/nickname")
    fun checkValidNickName(@Body user: User): Single<CommonResponse>

    @POST("/user/sms")
    fun sendSms(@Body authNumber: AuthNumber): Single<CommonResponse>

    @POST("/user/sms/config")
    fun checkSmsSecretValid(@Body authNumber: AuthNumber): Single<CommonResponse>

    @PUT("/user/password")
    fun changePassword(@Body user: User): Single<CommonResponse>

    @POST("/user/sign-up")
    fun signUp(@Body user: User): Single<CommonResponse>
}
