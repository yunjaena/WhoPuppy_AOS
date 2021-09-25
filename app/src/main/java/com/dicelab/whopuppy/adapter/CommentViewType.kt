package com.dicelab.whopuppy.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dicelab.whopuppy.R
import com.dicelab.whopuppy.base.recyclerview.BindingViewHolder
import com.dicelab.whopuppy.data.entity.CommentItem
import com.dicelab.whopuppy.databinding.ItemCommentContentBinding
import com.dicelab.whopuppy.databinding.ItemCommentWriteBinding

sealed class CommentViewType(val viewType: Int) {
    companion object {
        fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                COMMENT_WRITE_VIEW_TYPE -> {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_comment_write, parent, false)
                    BindingViewHolder<ItemCommentWriteBinding>(view)
                }
                else -> {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_comment_content, parent, false)
                    BindingViewHolder<ItemCommentContentBinding>(view)
                }
            }
        }
    }
}

data class CommentWriteView(val profileUrl: String?, val nickname: String, val articleId: Long) :
    CommentViewType(COMMENT_WRITE_VIEW_TYPE)

data class CommentContentView(val commentItem: CommentItem) :
    CommentViewType(COMMENT_CONTENT_VIEW_TYPE)

const val COMMENT_WRITE_VIEW_TYPE = 0
const val COMMENT_CONTENT_VIEW_TYPE = 1
