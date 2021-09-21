package com.dicelab.whopuppy.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dicelab.whopuppy.R
import com.dicelab.whopuppy.base.recyclerview.BindingViewHolder
import com.dicelab.whopuppy.data.Area
import com.dicelab.whopuppy.databinding.ItemAreaSelectBinding

class AreaSelectorAdapter(
    var selectArea: Area = Area.ALL
) : RecyclerView.Adapter<BindingViewHolder<ItemAreaSelectBinding>>() {
    private val areaList = Area.values()
    private var selectCheck: ArrayList<Int> = arrayListOf()

    init {
        for (area in areaList) {
            if (area == selectArea) {
                selectCheck.add(CHECKED)
            } else {
                selectCheck.add(UNCHECKED)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BindingViewHolder<ItemAreaSelectBinding> {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_area_select, parent, false)
        return BindingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BindingViewHolder<ItemAreaSelectBinding>, position: Int) {
        with(holder.binding.radioButton) {
            text = areaList[position].areaName
            isChecked = selectCheck[position] == CHECKED
            setOnClickListener {
                for (index in selectCheck.indices) {
                    selectCheck[index] = if (index == position) CHECKED else UNCHECKED
                }
                selectArea = areaList[position]
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int = areaList.size

    companion object {
        private const val UNCHECKED = 0
        private const val CHECKED = 1
    }
}
