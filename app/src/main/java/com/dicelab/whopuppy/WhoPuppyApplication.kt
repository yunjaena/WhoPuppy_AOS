package com.dicelab.whopuppy

import android.app.Application
import com.orhanobut.hawk.Hawk
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.dicelab.whopuppy.di.netWorkModule
import com.dicelab.whopuppy.di.repositoryModule
import com.dicelab.whopuppy.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class WhoPuppyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        Hawk.init(this@WhoPuppyApplication).build()
        Logger.addLogAdapter(object : AndroidLogAdapter() {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG
            }
        })
        startKoin {
            androidContext(this@WhoPuppyApplication)
            modules(netWorkModule)
            modules(repositoryModule)
            modules(viewModelModule)
        }
    }

    companion object {
        lateinit var instance: WhoPuppyApplication
            private set
    }
}
