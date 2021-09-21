package com.dicelab.whopuppy.adapter

import androidx.recyclerview.widget.DiffUtil
import com.dicelab.whopuppy.data.ChatRoomModel

class ChatRoomModelListDiffCallback(
    private val oldChatRoomModelList: List<ChatRoomModel>,
    private val newChatRoomModelList: List<ChatRoomModel>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldChatRoomModelList.size

    override fun getNewListSize(): Int = newChatRoomModelList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldChatRoomModelList[oldItemPosition].roomId == newChatRoomModelList[newItemPosition].roomId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldChatRoomModelList[oldItemPosition] == newChatRoomModelList[newItemPosition]
    }
}
