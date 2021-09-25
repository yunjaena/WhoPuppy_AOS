package com.dicelab.whopuppy.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicelab.whopuppy.R
import com.dicelab.whopuppy.base.recyclerview.BindingViewHolder
import com.dicelab.whopuppy.data.ChatRoomModel
import com.dicelab.whopuppy.databinding.ItemChatListBinding
import com.dicelab.whopuppy.util.goToChattingActivity

class ChatListAdapter : RecyclerView.Adapter<BindingViewHolder<ItemChatListBinding>>() {
    private val chatRoomModels = arrayListOf<ChatRoomModel>()

    fun update(updateChatRoomModels: List<ChatRoomModel>) {
        val diffUtilCallback = ChatRoomModelListDiffCallback(chatRoomModels, updateChatRoomModels)
        val differentItemResult = DiffUtil.calculateDiff(diffUtilCallback)
        chatRoomModels.clear()
        chatRoomModels.addAll(updateChatRoomModels)
        differentItemResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BindingViewHolder<ItemChatListBinding> {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_chat_list, parent, false)
        return BindingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BindingViewHolder<ItemChatListBinding>, position: Int) {
        val item = chatRoomModels[position]
        with(holder.binding) {
            Glide.with(holder.itemView.context)
                .load(item.createUserInfo?.profileImageUrl)
                .error(R.drawable.ic_brown_dog)
                .into(profileImageView)


            titleTextView.text = item.title
            dateTextView.text = item.updateDate
            contentTextView.text = item.content

            if (item.messageCount > 0) {
                messageCountTextView.visibility = View.VISIBLE
                messageCountTextView.text =
                    if (MAX_MESSAGE_COUNT > item.messageCount) item.messageCount.toString() else "${MAX_MESSAGE_COUNT}+"
            } else {
                messageCountTextView.visibility = View.INVISIBLE
            }
        }

        holder.itemView.setOnClickListener {
            holder.itemView.context.goToChattingActivity(roomId = item.roomId)
        }
    }

    override fun onViewRecycled(holder: BindingViewHolder<ItemChatListBinding>) {
        super.onViewRecycled(holder)
        Glide.with(holder.itemView.context).clear(holder.binding.profileImageView)
    }

    override fun getItemCount(): Int = chatRoomModels.size

    companion object {
        const val MAX_MESSAGE_COUNT = 300
    }
}
