package com.yunjaena.whopuppy.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import com.orhanobut.logger.Logger
import com.yunjaena.whopuppy.activity.AbandonedAnimalDetailActivity
import com.yunjaena.whopuppy.activity.AbandonedAnimalDetailActivity.Companion.EXTRA_ABANDONED_ITEM_INDEX
import com.yunjaena.whopuppy.activity.AbandonedAnimalSearchActivity

fun Context.goToAbandonedAnimalActivity() {
    if (this is Activity && isFinishing) {
        Logger.e("ActivityChanger activity is finished.")
        return
    }
    Intent(this, AbandonedAnimalSearchActivity::class.java).apply {
        addFlags(FLAG_ACTIVITY_SINGLE_TOP or FLAG_ACTIVITY_CLEAR_TOP)
    }.run {
        startActivity(this)
    }
}

fun Context.goToAbandonedAnimalDetailActivity(index: Long) {
    if (this is Activity && isFinishing) {
        Logger.e("ActivityChanger activity is finished.")
        return
    }
    Intent(this, AbandonedAnimalDetailActivity::class.java).apply {
        addFlags(FLAG_ACTIVITY_SINGLE_TOP or FLAG_ACTIVITY_CLEAR_TOP)
        putExtra(EXTRA_ABANDONED_ITEM_INDEX, index)
    }.run {
        startActivity(this)
    }
}
