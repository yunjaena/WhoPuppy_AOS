package com.dicelab.whopuppy.adapter

import androidx.recyclerview.widget.DiffUtil
import com.dicelab.whopuppy.data.entity.AbandonedAnimalItem

class AbandonListDiffCallback(
    private val oldItemAbandonAnimalList: ArrayList<AbandonedAnimalItem>,
    private val newItemAbandonAnimalList: ArrayList<AbandonedAnimalItem>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldItemAbandonAnimalList.size

    override fun getNewListSize(): Int = newItemAbandonAnimalList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItemAbandonAnimalList[oldItemPosition] == newItemAbandonAnimalList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItemAbandonAnimalList[oldItemPosition] == newItemAbandonAnimalList[newItemPosition]
    }
}
