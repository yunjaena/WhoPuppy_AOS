package com.yunjaena.whopuppy.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import com.orhanobut.logger.Logger
import com.yunjaena.whopuppy.activity.AbandonedDogSearchActivity

fun Context.goToAbandonedDogActivity() {
    if (this is Activity && isFinishing) {
        Logger.e("ActivityChanger activity is finished.")
        return
    }
    Intent(this, AbandonedDogSearchActivity::class.java).apply {
        addFlags(FLAG_ACTIVITY_SINGLE_TOP or FLAG_ACTIVITY_CLEAR_TOP)
    }.run {
        startActivity(this)
    }
}
