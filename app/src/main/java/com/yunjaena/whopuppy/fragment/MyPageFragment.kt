package com.yunjaena.whopuppy.fragment

import com.yunjaena.whopuppy.R
import com.yunjaena.whopuppy.base.fragment.ViewBindingFragment
import com.yunjaena.whopuppy.databinding.FragmentMyInfoBinding

class MyPageFragment : ViewBindingFragment<FragmentMyInfoBinding>() {
    override val layoutId: Int = R.layout.fragment_my_info

    companion object {
        const val TAG = "MyPageFragment"
        fun newInstance(): MyPageFragment = MyPageFragment()
    }
}
