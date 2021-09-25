package com.dicelab.whopuppy.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import com.dicelab.whopuppy.activity.AbandonedAnimalDetailActivity
import com.dicelab.whopuppy.activity.AbandonedAnimalDetailActivity.Companion.EXTRA_ABANDONED_ITEM_INDEX
import com.dicelab.whopuppy.activity.AbandonedAnimalSearchActivity
import com.dicelab.whopuppy.activity.ArticleDetailActivity
import com.dicelab.whopuppy.activity.ArticleDetailActivity.Companion.EXTRA_ARTICLE_ID
import com.dicelab.whopuppy.activity.ArticleEditActivity
import com.dicelab.whopuppy.activity.ArticleWriteActivity
import com.dicelab.whopuppy.activity.ArticleWriteActivity.Companion.EXTRA_BOARD_ID
import com.dicelab.whopuppy.activity.ArticleWriteActivity.Companion.EXTRA_BOARD_TITLE
import com.dicelab.whopuppy.activity.BreedCheckActivity
import com.dicelab.whopuppy.activity.BreedCheckActivity.Companion.EXTRA_IMAGE_URL
import com.dicelab.whopuppy.activity.ChattingActivity
import com.dicelab.whopuppy.activity.DogImageUploadActivity
import com.dicelab.whopuppy.activity.LoginActivity
import com.dicelab.whopuppy.activity.MyInfoEditActivity
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.orhanobut.logger.Logger

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

fun Context.goToBreedCheckActivity(imageUrl: String) {
    if (this is Activity && isFinishing) {
        Logger.e("ActivityChanger activity is finished.")
        return
    }
    Intent(this, BreedCheckActivity::class.java).apply {
        addFlags(FLAG_ACTIVITY_SINGLE_TOP or FLAG_ACTIVITY_CLEAR_TOP)
        putExtra(EXTRA_IMAGE_URL, imageUrl)
    }.run {
        startActivity(this)
    }
}


fun Context.goToArticleWriteActivity(boarId: Long, boardTitle: String) {
    if (this is Activity && isFinishing) {
        Logger.e("ActivityChanger activity is finished.")
        return
    }
    Intent(this, ArticleWriteActivity::class.java).apply {
        addFlags(FLAG_ACTIVITY_SINGLE_TOP or FLAG_ACTIVITY_CLEAR_TOP)
        putExtra(EXTRA_BOARD_ID, boarId)
        putExtra(EXTRA_BOARD_TITLE, boardTitle)
    }.run {
        startActivity(this)
    }
}

fun Context.goToArticleEditActivity(articleId: Long) {
    if (this is Activity && isFinishing) {
        Logger.e("ActivityChanger activity is finished.")
        return
    }
    Intent(this, ArticleEditActivity::class.java).apply {
        addFlags(FLAG_ACTIVITY_SINGLE_TOP or FLAG_ACTIVITY_CLEAR_TOP)
        putExtra(ArticleEditActivity.EXTRA_ARTICLE_ID, articleId)
    }.run {
        startActivity(this)
    }
}

fun Context.goToArticleDetailActivity(articleId: Long) {
    if (this is Activity && isFinishing) {
        Logger.e("ActivityChanger activity is finished.")
        return
    }
    Intent(this, ArticleDetailActivity::class.java).apply {
        addFlags(FLAG_ACTIVITY_SINGLE_TOP or FLAG_ACTIVITY_CLEAR_TOP)
        putExtra(EXTRA_ARTICLE_ID, articleId)
    }.run {
        startActivity(this)
    }
}

fun Context.goToChattingActivity(articleId: Long? = null, roomId: Long? = null) {
    if (this is Activity && isFinishing) {
        Logger.e("ActivityChanger activity is finished.")
        return
    }
    Intent(this, ChattingActivity::class.java).apply {
        addFlags(FLAG_ACTIVITY_SINGLE_TOP or FLAG_ACTIVITY_CLEAR_TOP)
        putExtra(ChattingActivity.ROOM_ID, roomId)
        putExtra(ChattingActivity.ARTICLE_ID, articleId)
    }.run {
        startActivity(this)
    }
}
