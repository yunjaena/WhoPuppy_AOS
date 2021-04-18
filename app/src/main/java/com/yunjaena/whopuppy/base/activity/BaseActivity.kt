package com.yunjaena.whopuppy.base.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.yunjaena.whopuppy.R
import com.yunjaena.whopuppy.base.appbar.BaseAppBar
import io.reactivex.rxjava3.disposables.CompositeDisposable

open class BaseActivity : AppCompatActivity() {
    protected val compositeDisposable = CompositeDisposable()
    private var baseAppBar: BaseAppBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun setBaseAppBar(title: String = "") {
        if (supportActionBar == null)
            throw IllegalStateException("Can not found supportActionBar")

        baseAppBar = BaseAppBar(this@BaseActivity, supportActionBar)
        baseAppBar?.setUpActionBar()
        setAppBarTitle(title)
    }

    fun appBarLeftButtonClicked(callback: (View) -> Unit) {
        baseAppBar?.leftButtonClickListener = callback
    }

    fun appBarRightButtonClicked(callback: (View) -> Unit) {
        baseAppBar?.rightButtonClickListener = callback
    }

    fun setAppBarTitle(title: String) {
        baseAppBar?.setUpActionBar()
        if (!title.isNullOrEmpty())
            baseAppBar?.setTitle(title)
    }

    fun setBackKey() {
        val backButtonDrawable =
            ContextCompat.getDrawable(this@BaseActivity, R.drawable.ic_arrow_back_black) ?: return
        baseAppBar?.setLeftButtonDrawable(backButtonDrawable)
        appBarLeftButtonClicked {
            onBackPressed()
        }
    }

    override fun onDestroy() {
        if (!compositeDisposable.isDisposed)
            compositeDisposable.dispose()

        super.onDestroy()
    }
}
