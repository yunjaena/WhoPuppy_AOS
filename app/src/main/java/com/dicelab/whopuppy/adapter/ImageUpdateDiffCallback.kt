package com.dicelab.whopuppy.adapter

import android.net.Uri
import androidx.recyclerview.widget.DiffUtil

class ImageUpdateDiffCallback(
    private val oldImageUrlList: ArrayList<Uri>,
    private val newImageUrlList: ArrayList<Uri>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldImageUrlList.size

    override fun getNewListSize(): Int = newImageUrlList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldImageUrlList[oldItemPosition] == newImageUrlList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldImageUrlList[oldItemPosition] == newImageUrlList[newItemPosition]
    }
}
