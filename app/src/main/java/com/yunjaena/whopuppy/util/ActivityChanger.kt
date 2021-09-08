package com.yunjaena.whopuppy.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.orhanobut.logger.Logger
import com.yunjaena.whopuppy.activity.AbandonedAnimalDetailActivity
import com.yunjaena.whopuppy.activity.AbandonedAnimalDetailActivity.Companion.EXTRA_ABANDONED_ITEM_INDEX
import com.yunjaena.whopuppy.activity.AbandonedAnimalSearchActivity
import com.yunjaena.whopuppy.activity.DogImageUploadActivity
import com.yunjaena.whopuppy.activity.LoginActivity
import com.yunjaena.whopuppy.activity.MyInfoEditActivity

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

fun Context.goToOssLibraryActivity() {
    if (this is Activity && isFinishing) {
        Logger.e("ActivityChanger activity is finished.")
        return
    }

    Intent(this, OssLicensesMenuActivity::class.java).run {
        startActivity(this)
    }
}

fun Context.goToLoginActivity() {
    if (this is Activity && isFinishing) {
        Logger.e("ActivityChanger activity is finished.")
        return
    }

    Intent(this, LoginActivity::class.java).run {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(this)
    }
}

fun Context.goToMyInfoEditActivity() {
    if (this is Activity && isFinishing) {
        Logger.e("ActivityChanger activity is finished.")
        return
    }
    Intent(this, MyInfoEditActivity::class.java).apply {
        addFlags(FLAG_ACTIVITY_SINGLE_TOP or FLAG_ACTIVITY_CLEAR_TOP)
    }.run {
        startActivity(this)
    }
}

fun Context.goToDogImageUploadActivity() {
    if (this is Activity && isFinishing) {
        Logger.e("ActivityChanger activity is finished.")
        return
    }
    Intent(this, DogImageUploadActivity::class.java).apply {
        addFlags(FLAG_ACTIVITY_SINGLE_TOP or FLAG_ACTIVITY_CLEAR_TOP)
    }.run {
        startActivity(this)
    }
}
