package com.yunjaena.whopuppy.util

import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import com.orhanobut.logger.Logger
import com.yunjaena.whopuppy.base.viewmodel.ViewModelBase
import com.yunjaena.whopuppy.data.UserRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleTransformer
import io.reactivex.rxjava3.schedulers.Schedulers
import org.koin.java.KoinJavaComponent.inject
import retrofit2.HttpException
import java.util.concurrent.TimeUnit

fun <T> Single<T>.withThread(): Single<T> {
    return this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

fun <T> Single<T>.handleUpdateAccessToken(): Single<T> {
    return this.compose(retryOnNotAuthorized<T>())
}

// TODO : 공통 HttpException 처리
fun <T> Single<T>.handleHttpException(): Single<T> {
    return this.handleUpdateAccessToken()
        .doOnError {
            if (it !is HttpException) return@doOnError
            Logger.e("handle http exception : ${it.code()}")
            when (it.code()) {
            }
        }
}

fun Completable.toSingleConvert(): Single<Boolean> {
    return this.toSingleDefault(true)
        .onErrorReturnItem(false)
}

fun <T> Single<T>.handleProgress(viewModel: ViewModelBase): Single<T> {
    return this.doOnSubscribe { viewModel.isLoading.postValue(true) }
        .doOnError { viewModel.isLoading.postValue(false) }
        .doOnSuccess { viewModel.isLoading.postValue(false) }
        .doOnDispose { viewModel.isLoading.postValue(false) }
}

fun <T> Completable.handleProgress(viewModel: ViewModelBase): Completable {
    return this.doOnSubscribe { viewModel.isLoading.postValue(true) }
        .doOnError { viewModel.isLoading.postValue(false) }
        .doOnComplete { viewModel.isLoading.postValue(false) }
        .doOnDispose { viewModel.isLoading.postValue(false) }
}

private fun <T> retryOnNotAuthorized(): SingleTransformer<T, T> {
    return SingleTransformer<T, T> { upstream ->
        upstream.onErrorResumeNext { throwable ->
            if (throwable is HttpException && throwable.code() == 401) {
                val userRepository: UserRepository by inject(UserRepository::class.java)
                userRepository.refreshToken()
                    .doOnError { error -> Logger.e("token update error : $error") }
                    .flatMap {
                        Completable.complete().andThen(upstream)
                    }
            } else
                Single.error(throwable)
        }
    }
}

fun EditText.debounce(
    time: Long = 500L,
    timeUnit: TimeUnit = TimeUnit.MILLISECONDS
): Observable<String> {
    return Observable.create { emitter: ObservableEmitter<String>? ->
        this.addTextChangedListener { emitter?.onNext(it.toString()) }
    }.debounce(time, timeUnit)
        .observeOn(AndroidSchedulers.mainThread())
}
