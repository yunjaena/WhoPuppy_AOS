package com.dicelab.whopuppy.base

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources.getSystem
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.transition.Slide
import android.transition.TransitionManager
import android.util.DisplayMetrics
import android.view.Display
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.dicelab.whopuppy.R


fun Context.showProgressDialog(userDim: Boolean = true): AlertDialog {
    val builder: AlertDialog.Builder = AlertDialog.Builder(this)
    builder.setCancelable(false)
    builder.setView(R.layout.layout_loading_dialog)
    val dialog: AlertDialog = builder.create()
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    if (!userDim)
        dialog.window?.setDimAmount(0F)
    dialog.show()
    return dialog
}

fun AlertDialog.hideProgressDialog() {
    this.dismiss()
}

fun Context.showAlertDialog(
    title: String,
    message: String,
    positiveButtonText: String,
    positiveCallback: (DialogInterface) -> Unit
) {
    val alertDialog = AlertDialog.Builder(this).apply {
        setTitle(title)
        setMessage(message)
        setCancelable(false)
        setPositiveButton(positiveButtonText) { dialog, _ ->
            positiveCallback.invoke(dialog)
        }
    }
    val dialog = alertDialog.create()
    dialog.setOnShowListener {
        dialog.window?.setBackgroundDrawableResource(
            R.drawable.bg_alert_dialog
        )
    }
    dialog.show()
}

fun Context.showAlertDialog(
    title: String,
    message: String,
    positiveButtonText: String,
    cancelButtonText: String,
    positiveCallback: ((DialogInterface) -> Unit)? = null,
    cancelCallback: ((DialogInterface) -> Unit)? = null,
) {
    val alertDialog = AlertDialog.Builder(this, R.style.AlertDialog).apply {
        setTitle(title)
        setMessage(message)
        setPositiveButton(positiveButtonText) { dialog, _ ->
            positiveCallback?.invoke(dialog) ?: dialog.dismiss()
        }
        setNegativeButton(cancelButtonText) { dialog, _ ->
            cancelCallback?.invoke(dialog) ?: dialog.dismiss()
        }
    }
    val dialog = alertDialog.create()
    dialog.setOnShowListener {
        dialog.window?.setBackgroundDrawableResource(
            R.drawable.bg_alert_dialog
        )
    }
    dialog.show()
}

fun Int.dpToPx() = this.toFloat().dpToPx()

fun Float.dpToPx() = (this * getSystem().displayMetrics.density).toInt()

fun Int.pxToDp() = this.toFloat().pxToDp()

fun Float.pxToDp() = (this / getSystem().displayMetrics.density).toInt()

fun Activity.getScreenWidth(): Int {
    var width: Int = 0
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val displayMetrics = DisplayMetrics()
        val display: Display? = this.display
        display!!.getRealMetrics(displayMetrics)
        return displayMetrics.widthPixels
    } else {
        val displayMetrics = DisplayMetrics()
        this.windowManager.defaultDisplay.getMetrics(displayMetrics)
        width = displayMetrics.widthPixels
        return width
    }
}

fun Activity.getScreenHeight(): Int {
    var height: Int = 0
    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
            val displayMetrics = DisplayMetrics()
            val display = this.display
            display!!.getRealMetrics(displayMetrics)
            displayMetrics.heightPixels
        }
        else -> {
            val displayMetrics = DisplayMetrics()
            this.windowManager.defaultDisplay.getMetrics(displayMetrics)
            height = displayMetrics.heightPixels
            height
        }
    }
}

fun Activity.hideKeyBoard() {
    val inputManager: InputMethodManager =
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val currentFocusedView: View? = currentFocus
    if (currentFocusedView != null) {
        inputManager.hideSoftInputFromWindow(
            currentFocusedView.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}

fun View.slideVisibility(visibility: Boolean, durationTime: Long = 300) {
    val transition = Slide(Gravity.BOTTOM)
    transition.apply {
        duration = durationTime
        addTarget(this@slideVisibility)
    }
    TransitionManager.beginDelayedTransition(this.parent as ViewGroup, transition)
    isVisible = visibility
}

fun View.fadeInAnimation(durationTime: Long = 300) {
    visibility = View.VISIBLE
    animate()
        .alpha(1f)
        .setDuration(durationTime)
        .setListener(null)
}

fun View.fadeOutAnimation(durationTime: Long = 300) {
    animate().alpha(0f)
        .setDuration(durationTime)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                visibility = View.GONE
            }
        })
}

fun Activity.getStatusBarHeight(): Int {
    var result = 0
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        result = resources.getDimensionPixelSize(resourceId)
    }
    return result
}


fun AppCompatActivity.handleBackPressedListener(
    interval: Long = 300L,
    onBackPressed: (() -> Boolean)? = null,
    singleClickAction: (() -> Unit)? = null,
    doubleClickAction: (() -> Unit)? = null
) {
    var isBackPressed = false
    onBackPressedDispatcher.addCallback(this) {
        if (onBackPressed?.invoke() != true) return@addCallback
        if (isBackPressed) {
            doubleClickAction?.invoke()
            return@addCallback
        }
        isBackPressed = true
        singleClickAction?.invoke()
        Handler(Looper.getMainLooper()).postDelayed({ isBackPressed = false }, interval)
    }
}
