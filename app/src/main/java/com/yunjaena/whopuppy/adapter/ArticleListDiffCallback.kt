package com.yunjaena.whopuppy.adapter

import androidx.recyclerview.widget.DiffUtil
import com.yunjaena.whopuppy.data.entity.ArticleItem

class ArticleListDiffCallback(
    private val oldArticleList: ArrayList<ArticleItem>,
    private val newArticleList: ArrayList<ArticleItem>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldArticleList.size

    override fun getNewListSize(): Int = newArticleList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldArticleList[oldItemPosition] == newArticleList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldArticleList[oldItemPosition] == newArticleList[newItemPosition]
    }
}
