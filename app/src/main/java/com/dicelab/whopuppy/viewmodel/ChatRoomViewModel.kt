package com.dicelab.whopuppy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dicelab.whopuppy.adapter.ChatModel
import com.dicelab.whopuppy.base.viewmodel.ViewModelBase
import com.dicelab.whopuppy.data.ChatRepository
import com.dicelab.whopuppy.data.ChatRoomModel
import com.dicelab.whopuppy.data.CommunityRepository
import com.dicelab.whopuppy.data.Message
import com.dicelab.whopuppy.data.UserRepository
import com.dicelab.whopuppy.data.entity.ArticleItem
import com.dicelab.whopuppy.data.entity.ChatMessage
import com.dicelab.whopuppy.data.entity.ChatRoomCreateItem
import com.dicelab.whopuppy.data.entity.User
import com.dicelab.whopuppy.data.entity.toChatModelList
import com.dicelab.whopuppy.data.entity.toChatRoomModels
import com.dicelab.whopuppy.util.SingleLiveEvent
import com.dicelab.whopuppy.util.handleProgress
import com.dicelab.whopuppy.util.retry
import com.dicelab.whopuppy.util.withThread
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import io.reactivex.rxjava3.kotlin.addTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class ChatRoomViewModel(
    private val chatRepository: ChatRepository,
    private val communityRepository: CommunityRepository,
    private val userRepository: UserRepository
) : ViewModelBase() {
    private var roomId: Long? = null
    private var topMessageId = 0L
    private var recentMessageId = 0L
    var firstMessageId = 0L
    var isTopEnd = false
    var isBottomEnd = false
    var isTopLoading = false
    var isBottomLoading = false
    var isItemFirstLoaded = false

    val errorEvent = SingleLiveEvent<Boolean>()
    val chatRoomList: LiveData<List<ChatRoomModel>>
        get() = _chatRoomList
    private val _chatRoomList = MutableLiveData<List<ChatRoomModel>>()
    val chatRoomName: LiveData<String>
        get() = _chatRoomName
    private val _chatRoomName = MutableLiveData<String>()
    val message = MutableLiveData<String>()
    private var subscribeJob: Job? = null
    var isChatRoomFoundEvent = SingleLiveEvent<Boolean>()

    val articleInfo: LiveData<ArticleItem>
        get() = _articleInfo
    private val _articleInfo = MutableLiveData<ArticleItem>()
    private val chatMessage = arrayListOf<ChatMessage>()
    val chatList: LiveData<List<ChatModel>>
        get() = _chatList
    private val _chatList = MutableLiveData<List<ChatModel>>()
    val newMessageEvent = SingleLiveEvent<Boolean>()
    val userProfileImage: LiveData<String>
        get() = _userProfileImage
    private val _userProfileImage = MutableLiveData<String>()

    fun getMyProfileImage() {
        viewModelScope.launch {
            val profileImageUrl = userRepository.getUserInfoCoroutine().profileImageUrl
            if (profileImageUrl != null)
                _userProfileImage.value = profileImageUrl!!
        }
    }

    fun connectChatSever() {
        viewModelScope.launch {
            try {
                chatRepository.connectToChatSever()
                subscribe()
                isBottomEnd = false
            } catch (e: Exception) {
            }
        }
    }

    fun getLastMessage(): ChatModel? {
        if (_chatList.value.isNullOrEmpty()) return null
        return _chatList.value!!.last()
    }

    suspend fun subscribe() {
        coroutineScope {
            subscribeJob?.cancelAndJoin()
            subscribeJob = launch {
                val id = userRepository.getUserInfoCoroutine().id
                chatRepository.subscribeTopic("/sub/chat/users/$id").catch {
                    Logger.e("$message")
                    connectChatSever()
                }.collect {
                    val chatMessage = Gson().fromJson(it.bodyAsText, ChatMessage::class.java)
                    Logger.d(chatMessage)
                    if (chatMessage.chatRoomId == roomId && chatMessage.id != null) {
                        handleFetchMessage(listOf(chatMessage))
                        readMessage(chatMessage.id)
                        newMessageEvent.call()
                    }
                }
            }
        }
    }

    fun sendMessage(message: String) {
        viewModelScope.launch {
            try {
                if (roomId == null) return@launch
                val jsonString = Gson().toJson(Message(roomId!!, message))
                chatRepository.sendMessage("/pub/chat/message", jsonString)
            } catch (e: Exception) {
                Logger.e(e.message ?: "")
            }
        }
    }

    fun readMessage(messageId: Long) {
        viewModelScope.launch {
            try {
                chatRepository.sendMessage("/pub/chat/read/$messageId")
            } catch (e: Exception) {
                Logger.e(e.message ?: "")
            }
        }
    }

    fun disConnectChatServer() {
        viewModelScope.launch {
            subscribeJob?.cancelChildren()
            chatRepository.disconnectChatServer()
        }
    }

    fun getFirstIdIndex(): Int {
        return _chatList.value!!.indexOfFirst { it.id == firstMessageId }
    }

    private fun getChatRoomInfo() {
        viewModelScope.launch {
            if (roomId == null) return@launch
            val roomInfo = chatRepository.getChatRoomDetail(roomId!!)
            if (roomInfo.article != null)
                _articleInfo.value = roomInfo.article!!
            _chatRoomName.value = roomInfo.name ?: ""
            topMessageId = roomInfo.chatMessage?.id ?: 0L
            recentMessageId = roomInfo.chatMessage?.id ?: 0L
            firstMessageId = roomInfo.chatMessage?.id ?: 0L
            loadBeforeChat()
        }
    }

    fun loadBeforeChat() {
        viewModelScope.launch {
            if (topMessageId < 1) {
                isTopEnd = true
                isTopLoading = false
                return@launch
            }

            isTopLoading = true
            val beforeTopId = topMessageId
            val chatMessage = getRecentMessage(topMessageId)

            when {
                chatMessage.isNullOrEmpty() -> {
                    isTopEnd = true
                }
                chatMessage.last().id == beforeTopId -> {
                    isTopEnd = true
                    handleFetchMessage(chatMessage)
                }
                else -> {
                    isTopEnd = false
                    handleFetchMessage(chatMessage)
                }
            }
            topMessageId = chatMessage.last().id ?: beforeTopId
            isTopLoading = false
        }
    }

    fun loadAfterChat() {
        viewModelScope.launch {
            if (recentMessageId + PAGING_COUNT >= Long.MAX_VALUE) {
                isBottomEnd = true
                isBottomLoading = false
                return@launch
            }

            isBottomLoading = true
            val beforeRecentId = recentMessageId
            recentMessageId += PAGING_COUNT
            val chatMessage = getRecentMessage(recentMessageId)

            when {
                chatMessage.isNullOrEmpty() -> {
                    isBottomEnd = true
                }
                chatMessage.first().id == beforeRecentId -> {
                    isBottomEnd = true
                    handleFetchMessage(chatMessage)
                }
                else -> {
                    isBottomEnd = false
                    handleFetchMessage(chatMessage)
                }
            }

            recentMessageId = chatMessage.first().id ?: beforeRecentId
            isBottomLoading = false
        }
    }

    private suspend fun handleFetchMessage(newMessage: List<ChatMessage>) {
        coroutineScope {
            val id = userRepository.getUserInfoCoroutine().id
            val newList = withContext(Dispatchers.IO) {
                val originList = chatMessage
                (newMessage + originList).distinctBy(ChatMessage::id).sortedBy { it.id }
            }
            chatMessage.clear()
            chatMessage.addAll(newList)
            _chatList.value = newList.toChatModelList(id!!)
            readLastMessage()
        }
    }

    private fun readLastMessage() {
        if (!_chatList.value.isNullOrEmpty())
            readMessage(_chatList.value!!.last().id)
    }

    private suspend fun getRecentMessage(messageId: Long): ArrayList<ChatMessage> {
        return retry {
            chatRepository.getRecentChatMessage(
                PAGING_COUNT,
                messageId,
                roomId!!
            )
        }
    }

    fun createChatRoom(articleId: Long, title: String) {
        communityRepository.getArticle(articleId)
            .handleProgress(this)
            .withThread()
            .subscribe({
                if (it.userId != null)
                    createChatRoom(title, articleId, it.userId)
            }) {
                errorEvent.call()
            }.addTo(compositeDisposable)
    }

    private fun createChatRoom(title: String, articleId: Long, userId: Long) {
        viewModelScope.launch {
            try {
                val roomId = chatRepository.createChatRoom(
                    ChatRoomCreateItem(
                        articleId = articleId,
                        name = title,
                        users = listOf(User(id = userId))
                    )
                )
                getChatRoomInfoByRoomId(roomId)
            } catch (e: Exception) {
                errorEvent.call()
                Logger.e("${e.message}")
            }
        }
    }

    fun getChatRoomInfoByRoomId(roomId: Long) {
        if (isChatRoomFoundEvent.value == true) return
        viewModelScope.launch {
            isLoading.value = true
            try {
                isChatRoomFoundEvent.value = true
                this@ChatRoomViewModel.roomId = roomId
                getChatRoomInfo()
            } catch (e: HttpException) {
                errorEvent.call()
            } catch (e: Exception) {
                errorEvent.call()
            } finally {
                isLoading.value = false
            }
        }
    }

    fun getChatRoomInfoByArticleId(articleId: Long) {
        if (isChatRoomFoundEvent.value == true) return

        viewModelScope.launch {
            isLoading.value = true
            try {
                val chatRoom = retry { chatRepository.getChatRooms() }
                val chatRoomModel = withContext(Dispatchers.IO) {
                    chatRoom.toChatRoomModels().find { it.articleId == articleId }
                }
                if (chatRoomModel == null) {
                    isChatRoomFoundEvent.value = false
                } else {
                    isChatRoomFoundEvent.value = true
                    roomId = chatRoomModel.roomId
                    getChatRoomInfo()
                }
            } catch (e: HttpException) {
                errorEvent.call()
            } catch (e: Exception) {
                errorEvent.call()
            } finally {
                isLoading.value = false
            }
        }
    }

    companion object {
        const val PAGING_COUNT = 20
    }
}
