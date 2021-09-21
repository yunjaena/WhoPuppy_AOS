package com.dicelab.whopuppy.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicelab.whopuppy.R
import com.dicelab.whopuppy.base.recyclerview.BindingViewHolder
import com.dicelab.whopuppy.databinding.ItemImageUploadBinding

class ImageUploadAdapter(
    val imageRemoveCallback: ((Uri) -> Unit)? = null
) : RecyclerView.Adapter<BindingViewHolder<ItemImageUploadBinding>>() {
    private val imageUrls = arrayListOf<Uri>()

    fun updateItem(updateImageUrls: ArrayList<Uri>) {
        val diffUtilCallback = ImageUpdateDiffCallback(imageUrls, updateImageUrls)
        val differentItemResult = DiffUtil.calculateDiff(diffUtilCallback)
        imageUrls.clear()
        imageUrls.addAll(updateImageUrls)
        differentItemResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BindingViewHolder<ItemImageUploadBinding> {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image_upload, parent, false)
        return BindingViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: BindingViewHolder<ItemImageUploadBinding>,
        position: Int
    ) {
        val imageUrl = imageUrls[position]
        with(holder.binding) {
            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .into(selectImageView)
        }
        holder.itemView.setOnClickListener {
            imageRemoveCallback?.invoke(imageUrl)
        }
    }

    override fun onViewRecycled(holder: BindingViewHolder<ItemImageUploadBinding>) {
        super.onViewRecycled(holder)
        Glide.with(holder.itemView.context).clear(holder.binding.selectImageView)
    }

    override fun getItemCount(): Int = imageUrls.size
}
