package com.yunjaena.whopuppy.fragment

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import com.yunjaena.whopuppy.R
import com.yunjaena.whopuppy.base.dpToPx
import com.yunjaena.whopuppy.base.fragment.ViewBindingFragment
import com.yunjaena.whopuppy.databinding.FragmentHomeBinding
import com.yunjaena.whopuppy.util.LinearLayoutItemDecoration

class HomeFragment : ViewBindingFragment<FragmentHomeBinding>() {
    override val layoutId: Int = R.layout.fragment_home

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        initView()
    }

    private fun initView() {
        initRecyclerView()
    }

    private fun initRecyclerView() {
        binding.abandonedDogRecyclerView.addItemDecoration(
            LinearLayoutItemDecoration(
                LinearLayout.HORIZONTAL,
                Rect(10.dpToPx(), 0.dpToPx(), 10.dpToPx(), 0.dpToPx())
            )
        )
    }

    companion object {
        const val TAG = "HomeFragment"
        fun newInstance(): HomeFragment = HomeFragment()
    }
}
