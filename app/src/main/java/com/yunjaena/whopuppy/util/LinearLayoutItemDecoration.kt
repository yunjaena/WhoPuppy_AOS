package com.yunjaena.whopuppy.util

import android.graphics.Rect
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView

class LinearLayoutItemDecoration(private val orientation: Int, val space: Rect) :
    RecyclerView.ItemDecoration() {
    lateinit var recyclerView: RecyclerView
    lateinit var currentView: View
    lateinit var currentOutRect: Rect

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (!isOrientationValid())
            throw IllegalArgumentException("orientation is not valid")

        recyclerView = parent
        currentView = view
        currentOutRect = outRect

        when (orientation) {
            LinearLayout.VERTICAL -> setVerticalSpace()
            LinearLayout.HORIZONTAL -> setHorizontalSpace()
        }
    }

    private fun isOrientationValid() =
        orientation == LinearLayout.HORIZONTAL || orientation == LinearLayout.VERTICAL

    private fun setVerticalSpace() {
        currentOutRect.left = space.left
        currentOutRect.right = space.right
        currentOutRect.bottom = space.bottom

        if (isNotFirstItem()) {
            currentOutRect.top = space.top
        }
    }

    private fun setHorizontalSpace() {
        currentOutRect.top = space.top
        currentOutRect.right = space.right
        currentOutRect.bottom = space.bottom

        if (isNotFirstItem()) {
            currentOutRect.left = space.left
        }
    }

    private fun isFirstItem() = recyclerView.getChildAdapterPosition(currentView) == 0

    private fun isNotFirstItem() = !isFirstItem()
}
