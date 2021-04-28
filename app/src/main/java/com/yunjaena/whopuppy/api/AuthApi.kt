package com.yunjaena.whopuppy.api

import com.yunjaena.whopuppy.data.response.TokenResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.POST

interface AuthApi {
    @POST("/user/refresh")
    fun refreshToken(): Single<TokenResponse>
}
