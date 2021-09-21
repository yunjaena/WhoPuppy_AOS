package com.dicelab.whopuppy.api

import com.dicelab.whopuppy.data.entity.AbandonedAnimal
import com.dicelab.whopuppy.data.entity.AbandonedAnimalItem
import com.dicelab.whopuppy.data.entity.AuthNumber
import com.dicelab.whopuppy.data.entity.User
import com.dicelab.whopuppy.data.response.CommonResponse
import com.dicelab.whopuppy.data.response.TokenResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

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

    @GET("/animal")
    fun getAnimal(
        @Query("kindCd") kindCd: String?,
        @Query("limit") limit: Int?,
        @Query("noticeNo") noticeNo: String?,
        @Query("page") page: Int?,
        @Query("sexCd") sexCd: String?
    ): Single<AbandonedAnimal>

    @GET("/animal/{idx}")
    fun getAnimalDetail(@Path("idx") idx: Long): Single<AbandonedAnimalItem>
}
