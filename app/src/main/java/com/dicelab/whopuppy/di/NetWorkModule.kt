package com.dicelab.whopuppy.di

import com.orhanobut.hawk.Hawk
import com.orhanobut.logger.Logger
import com.dicelab.whopuppy.BuildConfig
import com.dicelab.whopuppy.api.AuthApi
import com.dicelab.whopuppy.api.NoAuthApi
import com.dicelab.whopuppy.constant.ACCESS_TOKEN
import com.dicelab.whopuppy.constant.AUTH
import com.dicelab.whopuppy.constant.AUTH_CHAT
import com.dicelab.whopuppy.constant.AUTH_FLASK_SERVER
import com.dicelab.whopuppy.constant.AUTH_FLASK_SERVER_ADDRESS
import com.dicelab.whopuppy.constant.NO_AUTH
import com.dicelab.whopuppy.constant.PRODUCTION_SERVER_BASE_URL
import com.dicelab.whopuppy.constant.REFRESH_AUTH
import com.dicelab.whopuppy.constant.REFRESH_TOKEN
import com.dicelab.whopuppy.constant.STAGE_SERVER_BASE_URL
import com.dicelab.whopuppy.constant.URL
import com.dicelab.whopuppy.util.TokenAuthenticator
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient
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

    factory {
        StompClient(OkHttpWebSocketClient(get(named(AUTH_CHAT))))
    }

    factory {
        OkHttpClient.Builder().apply {
            connectTimeout(10, TimeUnit.SECONDS)
            readTimeout(30, TimeUnit.SECONDS)
            writeTimeout(15, TimeUnit.SECONDS)
            addInterceptor(getHttpLoggingInterceptor())
            authenticator(TokenAuthenticator(get()))
        }
    }

    factory(named(NO_AUTH)) {
        get<OkHttpClient.Builder>().apply {
            authenticator(Authenticator.NONE)
        }.build()
    }

    factory(named(AUTH)) {
        get<OkHttpClient.Builder>().apply {
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
        get<OkHttpClient.Builder>().apply {
            authenticator(Authenticator.NONE)
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

    factory(named(AUTH_CHAT)) {
        get<OkHttpClient.Builder>().apply {
            pingInterval(10, TimeUnit.SECONDS)
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

    single(named(AUTH_FLASK_SERVER)) {
        Retrofit.Builder()
            .client(get(named(AUTH)))
            .baseUrl(AUTH_FLASK_SERVER_ADDRESS)
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

    single(named(AUTH_FLASK_SERVER)) {
        provideAuthApi(get(named(AUTH_FLASK_SERVER)))
    }

    single(named(REFRESH_AUTH)) {
        provideAuthApi(get(named(REFRESH_AUTH)))
    }
}

private fun getHttpLoggingInterceptor(): HttpLoggingInterceptor {
    return HttpLoggingInterceptor { message ->
        Logger.i(message)
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
}

fun provideNoAuthApi(retrofit: Retrofit): NoAuthApi = retrofit.create(NoAuthApi::class.java)

fun provideAuthApi(retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)
