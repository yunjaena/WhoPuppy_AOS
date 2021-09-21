package com.dicelab.whopuppy.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicelab.whopuppy.R
import com.dicelab.whopuppy.base.recyclerview.BindingViewHolder
import com.dicelab.whopuppy.databinding.ItemCommentContentBinding
import com.dicelab.whopuppy.databinding.ItemCommentWriteBinding

class CommentAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val comments = arrayListOf<CommentViewType>()
    var commentWriteViewClickListener: ((CommentWriteView) -> Unit)? = null

    fun updateItem(comments: List<CommentViewType>) {
        this.comments.clear()
        this.comments.addAll(comments)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CommentViewType.getViewHolder(parent, viewType)
    }

    override fun getItemViewType(position: Int): Int {
        return comments[position].viewType
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = comments[position]) {
            is CommentWriteView -> setCommentWriteView(holder, item)
            is CommentContentView -> setCommentContentView(holder, item)
        }
    }

    private fun setCommentWriteView(
        holder: RecyclerView.ViewHolder,
        commentWriteView: CommentWriteView
    ) {
        if (holder !is BindingViewHolder<*> || holder.binding !is ItemCommentWriteBinding) return

        Glide.with(holder.itemView.context)
            .load(commentWriteView.profileUrl)
            .error(R.drawable.ic_brown_dog)
            .into(holder.binding.profileImageView)

        holder.binding.addComentTextView.setOnClickListener {
            commentWriteViewClickListener?.invoke(commentWriteView)
        }
    }


    private fun setCommentContentView(
        holder: RecyclerView.ViewHolder,
        commentContentView: CommentContentView
    ) {
        if (holder !is BindingViewHolder<*> || holder.binding !is ItemCommentContentBinding) return

        with(holder.binding) {
            Glide.with(holder.itemView.context)
                .load(commentContentView.commentItem.userProfileImage)
                .error(R.drawable.ic_brown_dog)
                .into(profileImageView)

            nickNameTextView.text = commentContentView.commentItem.userNickname ?: ""
            dateTextView.text = commentContentView.commentItem.getUpdateDate() ?: ""
            commentTextView.text = commentContentView.commentItem.content ?: ""

        }
    }

    override fun getItemCount(): Int = comments.size
}
