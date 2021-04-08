package com.yunjaena.whopuppy

import android.app.Application
import com.orhanobut.hawk.Hawk
import com.yunjaena.whopuppy.di.dataSourceModule
import com.yunjaena.whopuppy.di.netWorkModule
import com.yunjaena.whopuppy.di.repositoryModule
import com.yunjaena.whopuppy.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class WhoPuppyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        Hawk.init(this@WhoPuppyApplication).build()
        startKoin {
            androidContext(this@WhoPuppyApplication)
            modules(netWorkModule)
            modules(dataSourceModule)
            modules(repositoryModule)
            modules(viewModelModule)
        }
    }

    companion object {
        lateinit var instance: WhoPuppyApplication
            private set
    }
}
