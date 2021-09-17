package com.yunjaena.whopuppy.base

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources.getSystem
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.transition.Slide
import android.transition.TransitionManager
import android.util.DisplayMetrics
import android.view.Display
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import com.yunjaena.whopuppy.R

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
