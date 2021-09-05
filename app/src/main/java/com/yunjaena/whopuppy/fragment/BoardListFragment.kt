package com.yunjaena.whopuppy.fragment

import com.yunjaena.whopuppy.R
import com.yunjaena.whopuppy.base.fragment.ViewBindingFragment
import com.yunjaena.whopuppy.databinding.FragmentBoardListBinding

class BoardListFragment : ViewBindingFragment<FragmentBoardListBinding>() {
    override val layoutId: Int = R.layout.fragment_board_list

    companion object {
        const val TAG = "BoardListFragment"
        fun newInstance(): BoardListFragment = BoardListFragment()
    }
}
