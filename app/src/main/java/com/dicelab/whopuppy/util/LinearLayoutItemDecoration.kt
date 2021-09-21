package com.dicelab.whopuppy.util

import android.graphics.Rect
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView

class LinearLayoutItemDecoration(private val orientation: Int, val space: Rect) :
    RecyclerView.ItemDecoration() {
    lateinit var recyclerView: RecyclerView
    lateinit var recyclerViewState: RecyclerView.State
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
        recyclerViewState = state

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

        if (isNotFirstItem()) {
            currentOutRect.top = space.top
        }

        if (isNotEndPosition()) {
            currentOutRect.bottom = space.bottom
        }
    }

    private fun setHorizontalSpace() {
        currentOutRect.top = space.top
        currentOutRect.bottom = space.bottom

        if (isNotFirstItem()) {
            currentOutRect.left = space.left
        }

        if (isNotEndPosition()) {
            currentOutRect.right = space.right
        }
    }

    private fun itemPosition() = recyclerView.getChildAdapterPosition(currentView)

    private fun isFirstItem() = itemPosition() == 0

    private fun isNotFirstItem() = !isFirstItem()

    private fun itemTotalSize() = recyclerViewState.itemCount

    private fun isEndPosition() = itemPosition() == itemTotalSize() - 1

    private fun isNotEndPosition() = !isEndPosition()
}
