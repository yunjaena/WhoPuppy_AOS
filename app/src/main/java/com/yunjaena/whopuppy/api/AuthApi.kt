package com.yunjaena.whopuppy.api

import com.yunjaena.whopuppy.data.entity.User
import com.yunjaena.whopuppy.data.response.CommonResponse
import com.yunjaena.whopuppy.data.response.TokenResponse
import io.reactivex.rxjava3.core.Single
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

interface AuthApi {
    @POST("/user/refresh")
    fun refreshToken(): Single<TokenResponse>

    @GET("/user/me")
    fun getUserInfo(): Single<User>

    @PUT("/user/nickname")
    fun updateNickName(@Body user: User): Single<CommonResponse>

    @Multipart
    @POST("/community/article/image")
    fun uploadCommunityImage(@Part multipartFile: MultipartBody.Part): Single<String>
}
