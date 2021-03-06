package com.yunjaena.whopuppy.base.activity

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.yunjaena.whopuppy.R
import com.yunjaena.whopuppy.base.appbar.BaseAppBar
import com.yunjaena.whopuppy.base.hideProgressDialog
import com.yunjaena.whopuppy.base.showProgressDialog
import com.yunjaena.whopuppy.base.viewmodel.ViewModelBase
import io.reactivex.rxjava3.disposables.CompositeDisposable

open class BaseActivity : AppCompatActivity() {
    protected val compositeDisposable = CompositeDisposable()
    private var baseAppBar: BaseAppBar? = null
    private var progressDialog: AlertDialog? = null

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

    fun useDefaultLoading(viewModelBase: ViewModelBase, useDim: Boolean = true) {
        viewModelBase.isLoading.observe(this) { isLoading ->
            when (isLoading) {
                true -> showProgress(useDim)
                false -> hideProgress()
            }
        }
    }

    fun showProgress(useDim: Boolean = true) {
        runOnUiThread {
            progressDialog?.hideProgressDialog()
            progressDialog = this@BaseActivity.showProgressDialog(useDim)
        }
    }

    fun hideProgress() {
        runOnUiThread {
            progressDialog?.hideProgressDialog()
            progressDialog = null
        }
    }

    override fun onDestroy() {
        if (!compositeDisposable.isDisposed)
            compositeDisposable.dispose()

        super.onDestroy()
    }
}
