package com.yunjaena.whopuppy.fragment

import com.yunjaena.whopuppy.R
import com.yunjaena.whopuppy.base.fragment.ViewBindingFragment
import com.yunjaena.whopuppy.databinding.FragmentChatListBinding

class ChatListFragment : ViewBindingFragment<FragmentChatListBinding>() {
    override val layoutId: Int = R.layout.fragment_chat_list

    companion object {
        const val TAG = "ChatListFragment"
        fun newInstance(): ChatListFragment = ChatListFragment()
    }
}
