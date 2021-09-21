package com.dicelab.whopuppy.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicelab.whopuppy.R
import com.dicelab.whopuppy.base.recyclerview.BindingViewHolder
import com.dicelab.whopuppy.databinding.ItemImageBinding

class ImageSliderAdapter : RecyclerView.Adapter<BindingViewHolder<ItemImageBinding>>() {
    private val imageList = arrayListOf<String>()

    fun setImageUrls(imageUrls: List<String>) {
        imageList.clear()
        imageList.addAll(imageUrls)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BindingViewHolder<ItemImageBinding> {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return BindingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BindingViewHolder<ItemImageBinding>, position: Int) {
        Glide.with(holder.itemView.context)
            .load(imageList[position])
            .into(holder.binding.imageView)
    }

    override fun onViewRecycled(holder: BindingViewHolder<ItemImageBinding>) {
        super.onViewRecycled(holder)
        Glide.with(holder.itemView.context).clear(holder.binding.imageView)
    }

    override fun getItemCount(): Int = imageList.size
}
