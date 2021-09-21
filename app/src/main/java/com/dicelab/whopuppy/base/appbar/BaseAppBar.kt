package com.dicelab.whopuppy.base.appbar

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import com.dicelab.whopuppy.R
import com.dicelab.whopuppy.util.setOnSingleClickListener

class BaseAppBar(
    private val context: Context,
    private val actionBar: ActionBar?
) {
    private var isActionBarSet = false
    private val customAppBarView: View by lazy {
        LayoutInflater.from(context).inflate(R.layout.app_bar_base, null)
    }
    var leftButtonClickListener: ((View) -> Unit)? = null
    var rightButtonClickListener: ((View) -> Unit)? = null

    fun setUpActionBar() {
        if (actionBar == null) return
        val params = ActionBar.LayoutParams(
            ActionBar.LayoutParams.MATCH_PARENT,
            ActionBar.LayoutParams.MATCH_PARENT
        )

        with(actionBar) {
            setCustomView(customAppBarView, params)
            setDisplayShowCustomEnabled(true)
            setDisplayHomeAsUpEnabled(false)
            setDisplayShowTitleEnabled(false)
            setDisplayShowHomeEnabled(false)
            val parent = customAppBarView.parent
            if (parent is Toolbar) {
                parent.setContentInsetsAbsolute(0, 0)
            }
        }
        isActionBarSet = true
    }

    fun setTitle(title: String) {
        checkCustomActionbarSet()
        val titleTextView = customAppBarView.findViewById<TextView>(R.id.title_text_view)
        titleTextView.text = title
    }

    fun setLeftButton(@DrawableRes resourceId: Int) {
        checkCustomActionbarSet()
        val leftButton = customAppBarView.findViewById<ImageView>(R.id.left_button)
        leftButton.visibility = View.VISIBLE
        leftButton.setImageResource(resourceId)
        leftButton.setOnSingleClickListener {
            leftButtonClickListener?.invoke(leftButton)
        }
    }

    fun setRightButton(@DrawableRes resourceId: Int) {
        checkCustomActionbarSet()
        val rightButton = customAppBarView.findViewById<ImageView>(R.id.right_button)
        rightButton.visibility = View.VISIBLE
        rightButton.setImageResource(resourceId)
        rightButton.setOnSingleClickListener {
            rightButtonClickListener?.invoke(rightButton)
        }
    }

    private fun checkCustomActionbarSet() {
        if (!isActionBarSet)
            throw IllegalStateException("Need to set action bar first")
    }
}
