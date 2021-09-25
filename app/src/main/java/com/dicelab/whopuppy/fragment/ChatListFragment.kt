package com.dicelab.whopuppy.fragment

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.view.View
import com.dicelab.whopuppy.R
import com.dicelab.whopuppy.adapter.ChatListAdapter
import com.dicelab.whopuppy.base.fragment.ViewBindingFragment
import com.dicelab.whopuppy.databinding.FragmentChatListBinding
import com.dicelab.whopuppy.util.goToChattingActivity
import com.dicelab.whopuppy.viewmodel.ChatListViewModel
import com.orhanobut.logger.Logger
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChatListFragment : ViewBindingFragment<FragmentChatListBinding>() {
    override val layoutId: Int = R.layout.fragment_chat_list
    private val chatListViewModel: ChatListViewModel by viewModel()
    private lateinit var chatListAdapter: ChatListAdapter
    lateinit var connectivityManager: ConnectivityManager
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            Logger.d("Network available")
            connectChatServer()
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            Logger.d("Network disable")
            disconnectChatServer()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        binding.vm = chatListViewModel
        binding.chatTextView.setOnClickListener {}
        connectivityManager =
            requireContext().applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        initView()
        initObserver()
    }

    private fun initView() {
        initRecyclerView()
    }

    private fun initRecyclerView() {
        chatListAdapter = ChatListAdapter()
        binding.chatRecyclerView.adapter = chatListAdapter
        binding.chatRecyclerView.itemAnimator = null
    }

    private fun initObserver() {
        with(chatListViewModel) {
            chatRoomList.observe(viewLifecycleOwner) {
                chatListAdapter.update(it)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        registerConnectCheck()
        connectChatServer()
    }

    override fun onPause() {
        super.onPause()
        unRegisterConnectCheck()
        disconnectChatServer()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        when (hidden) {
            true -> disconnectChatServer()
            false -> connectChatServer()
        }
    }

    private fun connectChatServer() {
        chatListViewModel.getChatRoom()
        chatListViewModel.connectChatSever()
    }

    private fun disconnectChatServer() {
        chatListViewModel.disConnectChatServer()
    }

    private fun registerConnectCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
        } else {
            val request =
                NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build()
            connectivityManager.registerNetworkCallback(request, networkCallback)
        }
    }

    private fun unRegisterConnectCheck() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    companion object {
        const val TAG = "ChatListFragment"
        fun newInstance(): ChatListFragment = ChatListFragment()
    }
}
