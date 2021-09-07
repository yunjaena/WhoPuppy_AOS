package com.yunjaena.whopuppy.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yunjaena.whopuppy.R
import com.yunjaena.whopuppy.base.recyclerview.BindingViewHolder
import com.yunjaena.whopuppy.data.entity.AbandonedDogItem
import com.yunjaena.whopuppy.databinding.ItemHomeAbandonedDogListBinding
import com.yunjaena.whopuppy.util.goToAbandonedDogDetailActivity

class AbandonDogListAdapter :
    RecyclerView.Adapter<BindingViewHolder<ItemHomeAbandonedDogListBinding>>() {
    private var abandonDogs: ArrayList<AbandonedDogItem> = arrayListOf()

    fun updateItem(updateAbandonDogs: ArrayList<AbandonedDogItem>) {
        val diffUtilCallback = AbandonListDiffCallback(abandonDogs, updateAbandonDogs)
        val differentItemResult = DiffUtil.calculateDiff(diffUtilCallback)
        abandonDogs.clear()
        abandonDogs.addAll(updateAbandonDogs)
        differentItemResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BindingViewHolder<ItemHomeAbandonedDogListBinding> {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_abandoned_dog_list, parent, false)
        return BindingViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: BindingViewHolder<ItemHomeAbandonedDogListBinding>,
        position: Int
    ) {
        val currentItem = abandonDogs[position]
        with(holder.binding) {
            Glide.with(holder.itemView.context)
                .load(currentItem.filename)
                .into(dogImageView)
            dogKindTextView.text = currentItem.kindCd
            dogAgeTextView.text = currentItem.age
        }
        holder.itemView.setOnClickListener {
            holder.itemView.context.goToAbandonedDogDetailActivity(currentItem.idx)
        }
    }

    override fun onViewRecycled(holder: BindingViewHolder<ItemHomeAbandonedDogListBinding>) {
        super.onViewRecycled(holder)
        Glide.with(holder.itemView.context).clear(holder.binding.dogImageView)
    }

    override fun getItemCount(): Int = abandonDogs.size
}
