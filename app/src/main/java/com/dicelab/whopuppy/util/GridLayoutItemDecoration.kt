package com.dicelab.whopuppy.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridLayoutItemDecoration(
    private val spanCount: Int,
    private val spacing: Int,
    private val includeEdge: Boolean = false
) : RecyclerView.ItemDecoration() {
    private var position: Int = 0
    private var column: Int = 0

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        position = parent.getChildAdapterPosition(view)
        column = position % spanCount
        when (includeEdge) {
            true -> updateGridSpaceWithEdge(outRect)
            false -> updateGridSpaceWithoutEdge(outRect)
        }
    }

    private fun updateGridSpaceWithEdge(outRect: Rect) {
        outRect.left = spacing - column * spacing / spanCount
        outRect.right = (column + 1) * spacing / spanCount

        if (position < spanCount) {
            outRect.top = spacing
        }
        outRect.bottom = spacing
    }

    private fun updateGridSpaceWithoutEdge(outRect: Rect) {
        outRect.left = column * spacing / spanCount
        outRect.right = spacing - (column + 1) * spacing / spanCount

        if (position >= spanCount) {
            outRect.top = spacing
        }
    }

}
