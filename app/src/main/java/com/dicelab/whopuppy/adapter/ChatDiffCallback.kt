package com.dicelab.whopuppy.adapter

import androidx.recyclerview.widget.DiffUtil

class ChatDiffCallback(
    private val oldChatList: List<ChatModel>,
    private val newChatList: List<ChatModel>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldChatList.size

    override fun getNewListSize(): Int = newChatList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldChatList[oldItemPosition].id == newChatList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldChatList[oldItemPosition] == newChatList[newItemPosition]
    }
}
