package com.dicelab.whopuppy.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicelab.whopuppy.R
import com.dicelab.whopuppy.base.recyclerview.BindingViewHolder
import com.dicelab.whopuppy.databinding.ItemChatMineBinding
import com.dicelab.whopuppy.databinding.ItemChatOpponentBinding

class ChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val chatList = arrayListOf<ChatModel>()

    fun update(newChatList: List<ChatModel>) {
        val diffUtilCallback = ChatDiffCallback(chatList, newChatList)
        val differentItemResult = DiffUtil.calculateDiff(diffUtilCallback)
        chatList.clear()
        chatList.addAll(newChatList)
        differentItemResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (getChatModelClass(viewType)) {
            MyChatText::class -> {
                BindingViewHolder<ItemChatMineBinding>(getView(parent, R.layout.item_chat_mine))
            }
            OpponentText::class -> {
                BindingViewHolder<ItemChatMineBinding>(getView(parent, R.layout.item_chat_opponent))
            }
            else -> {
                throw IllegalArgumentException("wrong viewType")
            }
        }
    }

    private fun getView(parent: ViewGroup, @LayoutRes layoutRes: Int): View {
        return LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
    }

    override fun getItemViewType(position: Int): Int {
        val item = chatList[position]
        return item.getViewType()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = chatList[position]
        if (holder !is BindingViewHolder<*>) return
        when (holder.binding) {
            is ItemChatMineBinding -> {
                initMyChat(holder.binding, item)
            }
            is ItemChatOpponentBinding -> {
                initOpponentChat(holder.binding, item)
            }
        }
    }

    private fun initMyChat(binding: ItemChatMineBinding, item: ChatModel) {
        if (item !is MyChatText) return
        binding.chatTextView.text = item.message
        binding.timeTextView.text = item.createdAt
    }

    private fun initOpponentChat(binding: ItemChatOpponentBinding, item: ChatModel) {
        if (item !is OpponentText) return
        binding.chatTextView.text = item.message
        binding.timeTextView.text = item.createdAt

        Glide.with(binding.root.context)
            .load(item.user.profileImageUrl)
            .error(R.drawable.ic_brown_dog)
            .into(binding.profileImageView)

        binding.nickNameTextView.text = item.user.nickname ?: ""
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder is BindingViewHolder<*> && holder.binding is ItemChatOpponentBinding) {
            Glide.with(holder.itemView).clear(holder.binding.profileImageView)
        }
    }

    override fun getItemCount(): Int = chatList.size
}
