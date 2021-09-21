package com.dicelab.whopuppy.base.activity

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dicelab.whopuppy.R
import com.dicelab.whopuppy.base.appbar.BaseAppBar
import com.dicelab.whopuppy.base.hideProgressDialog
import com.dicelab.whopuppy.base.showProgressDialog
import com.dicelab.whopuppy.base.viewmodel.ViewModelBase
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
        baseAppBar?.setLeftButton(R.drawable.ic_arrow_back_black)
        appBarLeftButtonClicked {
            onBackPressed()
        }
    }

    fun setCheckButton(callback: (View) -> Unit) {
        baseAppBar?.setRightButton(R.drawable.ic_check)
        appBarRightButtonClicked(callback)
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
