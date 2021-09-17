package com.yunjaena.whopuppy.fragment

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.view.View
import com.orhanobut.logger.Logger
import com.yunjaena.whopuppy.R
import com.yunjaena.whopuppy.base.fragment.ViewBindingFragment
import com.yunjaena.whopuppy.databinding.FragmentChatListBinding
import com.yunjaena.whopuppy.util.showToast
import com.yunjaena.whopuppy.viewmodel.ChatListViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChatListFragment : ViewBindingFragment<FragmentChatListBinding>() {
    override val layoutId: Int = R.layout.fragment_chat_list
    private val chatListViewModel: ChatListViewModel by viewModel()
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
        connectivityManager =
            requireContext().applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        binding.sendButton.setOnClickListener {
            val message = binding.chatEditText.text.toString()
            chatListViewModel.sendMessage(1, message)
        }
        chatListViewModel.message.observe(viewLifecycleOwner) {
            requireContext().showToast(it)
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
