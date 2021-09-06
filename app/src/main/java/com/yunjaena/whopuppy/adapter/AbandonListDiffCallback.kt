package com.yunjaena.whopuppy.adapter

import androidx.recyclerview.widget.DiffUtil
import com.yunjaena.whopuppy.data.entity.AbandonedDogItem

class AbandonListDiffCallback(
    private val oldItemAbandonDogList: ArrayList<AbandonedDogItem>,
    private val newItemAbandonDogList: ArrayList<AbandonedDogItem>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldItemAbandonDogList.size

    override fun getNewListSize(): Int = newItemAbandonDogList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItemAbandonDogList[oldItemPosition] == newItemAbandonDogList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItemAbandonDogList[oldItemPosition] == newItemAbandonDogList[newItemPosition]
    }
}
