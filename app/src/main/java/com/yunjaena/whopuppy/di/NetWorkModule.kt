package com.yunjaena.whopuppy.base.di

import com.orhanobut.hawk.Hawk
import com.orhanobut.logger.Logger
import com.yunjaena.whopuppy.BuildConfig
import com.yunjaena.whopuppy.base.constant.ACCESS_TOKEN
import com.yunjaena.whopuppy.base.constant.AUTH
import com.yunjaena.whopuppy.base.constant.NO_AUTH
import com.yunjaena.whopuppy.base.constant.REFRESH_AUTH
import com.yunjaena.whopuppy.base.constant.REFRESH_TOKEN
import com.yunjaena.whopuppy.base.constant.URL
import com.yunjaena.whopuppy.base.network.AuthApi
import com.yunjaena.whopuppy.base.network.NoAuthApi
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

val netWorkModule = module {
    factory(named(URL)) {
        if (BuildConfig.DEBUG)
            STAGE_SERVER_BASE_URL
        else
            PRODUCTION_SERVER_BASE_URL
    }

    factory(named(NO_AUTH)) {
        OkHttpClient.Builder().apply {
            connectTimeout(10, TimeUnit.SECONDS)
            readTimeout(30, TimeUnit.SECONDS)
            writeTimeout(15, TimeUnit.SECONDS)
            addInterceptor(getHttpLoggingInterceptor())
        }.build()
    }

    factory(named(AUTH)) {
        OkHttpClient.Builder().apply {
            connectTimeout(10, TimeUnit.SECONDS)
            readTimeout(30, TimeUnit.SECONDS)
            writeTimeout(15, TimeUnit.SECONDS)
            addInterceptor(getHttpLoggingInterceptor())
            addInterceptor { chain ->
                val accessToken = Hawk.get(ACCESS_TOKEN, "")
                Logger.d("access token : $accessToken")
                val newRequest: Request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $accessToken")
                    .build()
                chain.proceed(newRequest)
            }
        }.build()
    }

    factory(named(REFRESH_AUTH)) {
        OkHttpClient.Builder().apply {
            connectTimeout(10, TimeUnit.SECONDS)
            readTimeout(30, TimeUnit.SECONDS)
            writeTimeout(15, TimeUnit.SECONDS)
            addInterceptor(getHttpLoggingInterceptor())
            addInterceptor { chain ->
                val refreshToken = Hawk.get(REFRESH_TOKEN, "")
                Logger.d("refresh token : $refreshToken")
                val newRequest: Request = chain.request().newBuilder()
                    .addHeader("RefreshToken", "Bearer $refreshToken")
                    .build()
                chain.proceed(newRequest)
            }
        }.build()
    }

    single(named(NO_AUTH)) {
        Retrofit.Builder()
            .client(get(named(NO_AUTH)))
            .baseUrl(get<String>(named(URL)))
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
    }

    single(named(AUTH)) {
        Retrofit.Builder()
            .client(get(named(AUTH)))
            .baseUrl(get<String>(named(URL)))
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
    }

    single(named(REFRESH_AUTH)) {
        Retrofit.Builder()
            .client(get(named(REFRESH_AUTH)))
            .baseUrl(get<String>(named(URL)))
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
    }

    single(named(NO_AUTH)) {
        provideNoAuthApi(get(named(NO_AUTH)))
    }

    single(named(AUTH)) {
        provideAuthApi(get(named(AUTH)))
    }

    single(named(REFRESH_AUTH)) {
        provideAuthApi(get(named(REFRESH_AUTH)))
    }
}

private fun getHttpLoggingInterceptor(): HttpLoggingInterceptor {
    return HttpLoggingInterceptor { message ->
        if (BuildConfig.DEBUG)
            Logger.i(message)
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
}

fun provideNoAuthApi(retrofit: Retrofit): NoAuthApi = retrofit.create(NoAuthApi::class.java)

fun provideAuthApi(retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)
