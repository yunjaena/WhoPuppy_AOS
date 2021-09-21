package com.dicelab.whopuppy.activity

import android.content.Intent
import android.os.Bundle
import com.dicelab.whopuppy.R
import com.dicelab.whopuppy.base.activity.ViewBindingActivity
import com.dicelab.whopuppy.databinding.ActivitySplashBinding
import com.dicelab.whopuppy.viewmodel.SplashViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.addTo
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class SplashActivity : ViewBindingActivity<ActivitySplashBinding>() {
    override val layoutId: Int = R.layout.activity_splash
    private val splashViewModel: SplashViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        initObserver()
        Observable.timer(2L, TimeUnit.SECONDS)
            .take(1)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                splashViewModel.checkAutoLogin()
            }.addTo(compositeDisposable)
    }

    private fun initObserver() {
        with(splashViewModel) {
            isLoginSuccess.observe(this@SplashActivity) {
                when (it) {
                    true -> goToMainActivity()
                    false -> goToLoginActivity()
                }
            }
        }
    }

    private fun goToLoginActivity() {
        startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
        finish()
    }

    private fun goToMainActivity() {
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        finish()
    }
}
