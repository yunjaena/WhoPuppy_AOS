package com.dicelab.whopuppy.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicelab.whopuppy.R
import com.dicelab.whopuppy.adapter.ChatAdapter
import com.dicelab.whopuppy.adapter.OpponentText
import com.dicelab.whopuppy.base.activity.ViewBindingActivity
import com.dicelab.whopuppy.data.entity.ArticleItem
import com.dicelab.whopuppy.databinding.ActivityChattingBinding
import com.dicelab.whopuppy.dialog.TitleInputDialog
import com.dicelab.whopuppy.util.goToArticleDetailActivity
import com.dicelab.whopuppy.util.showTitleInputDialog
import com.dicelab.whopuppy.util.showToast
import com.dicelab.whopuppy.viewmodel.ChatRoomViewModel
import com.orhanobut.logger.Logger
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChattingActivity : ViewBindingActivity<ActivityChattingBinding>() {
    override val layoutId: Int = R.layout.activity_chatting
    private val chatRoomViewModel: ChatRoomViewModel by viewModel()
    lateinit var connectivityManager: ConnectivityManager
    lateinit var chatAdapter: ChatAdapter
    private var isScrolling = false
    private var articleId: Long = -1
    private var roomId: Long = -1
    private var totalScroll = 0

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

    private val recyclerViewScrollCallback = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (!recyclerView.canScrollVertically(-1) && !isTopLoading() && !isTopEnd()) {
                runOnUiThread {
                    chatRoomViewModel.loadBeforeChat()
                }
            } else if (!recyclerView.canScrollVertically(1) && !isBottomLoading() && !isBottomEnd()) {
                runOnUiThread {
                    chatRoomViewModel.loadAfterChat()
                }
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            totalScroll += dy
            val linearLayoutManager =
                binding.chatRecyclerView.layoutManager as LinearLayoutManager
            checkBottomMessageIsShowing()

            if (linearLayoutManager.findFirstVisibleItemPosition() <= LIST_THRESHOLD && !isTopLoading() && !isTopEnd()) {
                runOnUiThread {
                    chatRoomViewModel.loadBeforeChat()
                }
            } else if (linearLayoutManager.findLastVisibleItemPosition() + LIST_THRESHOLD <= linearLayoutManager.itemCount - 1 && !isBottomLoading() && !isBottomEnd()) {
                runOnUiThread {
                    chatRoomViewModel.loadAfterChat()
                }
            }
        }
    }

    private fun checkBottomMessageIsShowing() {
        val linearLayoutManager =
            binding.chatRecyclerView.layoutManager as LinearLayoutManager
        isScrolling = linearLayoutManager.findLastVisibleItemPosition() < chatAdapter.itemCount - 1
    }

    private val listener = object : RecyclerView.AdapterDataObserver() {
        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            val linearLayoutManager = binding.chatRecyclerView.layoutManager as LinearLayoutManager
            if (!chatRoomViewModel.isItemFirstLoaded) {
                binding.chatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)
                chatRoomViewModel.isItemFirstLoaded = true
                isScrolling = false
                return
            }

            if (linearLayoutManager.findLastVisibleItemPosition() >= chatAdapter.itemCount - 2) {
                binding.chatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)
            }
        }
    }

    private fun showLastMessage() {
        (chatRoomViewModel.getLastMessage() as? OpponentText)?.let {
            Glide.with(this)
                .load(it.user.profileImageUrl)
                .error(R.drawable.ic_brown_dog)
                .into(binding.profileImageView)
            binding.nickNameTextView.text = it.user.nickname ?: ""
            binding.newMessageTextView.text = it.message
            fadeInFadeOutNewMessageLayout()
        }
    }

    private fun fadeInFadeOutNewMessageLayout() {
        binding.newMessageLayout.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate()
                .alpha(1f)
                .setDuration(100L)
                .setListener(null)
                .withEndAction {
                    alpha = 1f
                    visibility = View.VISIBLE
                    animate()
                        .alpha(0f)
                        .setStartDelay(1000L)
                        .setDuration(100L)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                binding.newMessageLayout.visibility = View.GONE
                            }
                        })
                }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        setBaseAppBar(getString(R.string.chat))
        setBackKey()
        connectivityManager =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        initView()
        initChatRoomTitleListener()
        initObserver()
        useDefaultLoading(chatRoomViewModel)
        articleId = intent.getLongExtra(ARTICLE_ID, -1)
        roomId = intent.getLongExtra(ROOM_ID, -1)

        when {
            roomId != -1L -> {
                chatRoomViewModel.getChatRoomInfoByRoomId(roomId)
            }
            articleId != -1L -> {
                chatRoomViewModel.getChatRoomInfoByArticleId(articleId)
            }
            else -> {
                showToast(getString(R.string.wrong_access))
                finish()
                return
            }
        }
        chatRoomViewModel.getMyProfileImage()
    }

    private fun initView() {
        binding.sendMessageEditText.isPressed = true
        binding.sendMessageButton.setOnClickListener {
            val message = binding.sendMessageEditText.text.toString().trim()
            if (message.isEmpty()) return@setOnClickListener
            binding.sendMessageEditText.setText("")
            chatRoomViewModel.sendMessage(message)
            binding.chatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)
        }
        binding.newMessageLayout.setOnClickListener {
            binding.chatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)
        }
        initRecyclerView()
    }

    private fun initRecyclerView() {
        chatAdapter = ChatAdapter()
        binding.chatRecyclerView.itemAnimator = null
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        binding.chatRecyclerView.addOnScrollListener(recyclerViewScrollCallback)
        binding.chatRecyclerView.adapter = chatAdapter
        chatAdapter.registerAdapterDataObserver(listener)
    }

    private fun initObserver() {
        with(chatRoomViewModel) {
            articleInfo.observe(this@ChattingActivity) {
                if (it != null)
                    setArticleInfo(it)
            }

            chatRoomName.observe(this@ChattingActivity) {
                setAppBarTitle(it)
            }

            chatList.observe(this@ChattingActivity) {
                if (!it.isNullOrEmpty()) {
                    chatAdapter.update(it)
                }
            }

            newMessageEvent.observe(this@ChattingActivity) {
                if (isScrolling) {
                    showLastMessage()
                }
            }

            userProfileImage.observe(this@ChattingActivity) {
                Glide.with(this@ChattingActivity)
                    .load(it)
                    .error(R.drawable.ic_brown_dog)
                    .into(binding.myProfileImageView)
            }

            isChatRoomFoundEvent.observe(this@ChattingActivity) {
                if (it == false) {
                    supportFragmentManager.showTitleInputDialog()
                }
            }

            errorEvent.observe(this@ChattingActivity) {
                showToast(getString(R.string.error))
                finish()
            }
        }
    }

    private fun initChatRoomTitleListener() {
        supportFragmentManager.setFragmentResultListener(
            TitleInputDialog.TAG,
            this
        ) { _, bundle ->
            val result = bundle.getInt(TitleInputDialog.RESULT)
            if (result == TitleInputDialog.RESULT_CANCEL) {
                finish()
                return@setFragmentResultListener
            }
            val title = bundle.getString(TitleInputDialog.TITLE) ?: ""
            chatRoomViewModel.createChatRoom(articleId, title)
        }
    }

    private fun setArticleInfo(articleItem: ArticleItem) {
        Glide.with(this)
            .load(articleItem.thumbnail)
            .error(R.drawable.ic_brown_dog)
            .into(binding.articleThumbnailImageView)

        binding.articleTitle.text = articleItem.title ?: ""

        binding.goToArticleButton.setOnClickListener {
            if (articleItem.id != null)
                goToArticleDetailActivity(articleItem.id)
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

    private fun connectChatServer() {
        chatRoomViewModel.connectChatSever()
    }

    private fun disconnectChatServer() {
        chatRoomViewModel.disConnectChatServer()
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

    private fun isTopLoading(): Boolean = chatRoomViewModel.isTopLoading
    private fun isBottomLoading(): Boolean = chatRoomViewModel.isBottomLoading
    private fun isTopEnd(): Boolean = chatRoomViewModel.isTopEnd
    private fun isBottomEnd(): Boolean = chatRoomViewModel.isBottomEnd

    override fun onDestroy() {
        binding.chatRecyclerView.removeOnScrollListener(recyclerViewScrollCallback)
        chatAdapter.unregisterAdapterDataObserver(listener)
        super.onDestroy()
    }

    companion object {
        const val LIST_THRESHOLD = 5
        const val ARTICLE_ID = "ARTICLE_ID"
        const val ROOM_ID = "ROOM_ID"
    }
}
