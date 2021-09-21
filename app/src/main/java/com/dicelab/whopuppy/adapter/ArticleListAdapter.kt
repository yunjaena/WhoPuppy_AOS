package com.dicelab.whopuppy.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicelab.whopuppy.R
import com.dicelab.whopuppy.base.recyclerview.BindingViewHolder
import com.dicelab.whopuppy.data.entity.ArticleItem
import com.dicelab.whopuppy.databinding.ItemArticleBinding
import com.dicelab.whopuppy.util.goToArticleDetailActivity

class ArticleListAdapter :
    RecyclerView.Adapter<BindingViewHolder<ItemArticleBinding>>() {
    private var articles: ArrayList<ArticleItem> = arrayListOf()

    fun updateItem(updateArticles: ArrayList<ArticleItem>) {
        val diffUtilCallback = ArticleListDiffCallback(articles, updateArticles)
        val differentItemResult = DiffUtil.calculateDiff(diffUtilCallback)
        articles.clear()
        articles.addAll(updateArticles)
        differentItemResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BindingViewHolder<ItemArticleBinding> {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_article, parent, false)
        return BindingViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: BindingViewHolder<ItemArticleBinding>,
        position: Int
    ) {
        val currentItem = articles[position]
        with(holder.binding) {
            Glide.with(holder.itemView.context)
                .load(currentItem.thumbnail)
                .into(thumbnailImageView)
            articleTitle.text = currentItem.title ?: ""
            articleContent.text = currentItem.content ?: ""
        }
        holder.itemView.setOnClickListener {
            holder.itemView.context.goToArticleDetailActivity(currentItem.id ?: 0)
        }
    }

    override fun onViewRecycled(holder: BindingViewHolder<ItemArticleBinding>) {
        super.onViewRecycled(holder)
        Glide.with(holder.itemView.context).clear(holder.binding.thumbnailImageView)
    }

    override fun getItemCount(): Int = articles.size
}
