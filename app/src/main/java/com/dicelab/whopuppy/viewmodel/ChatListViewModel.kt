package com.dicelab.whopuppy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dicelab.whopuppy.base.viewmodel.ViewModelBase
import com.dicelab.whopuppy.data.ChatRepository
import com.dicelab.whopuppy.data.ChatRoomModel
import com.dicelab.whopuppy.data.Message
import com.dicelab.whopuppy.data.UserRepository
import com.dicelab.whopuppy.data.entity.toChatRoomModels
import com.dicelab.whopuppy.util.retry
import com.google.gson.Gson
import com.orhanobut.logger.Logger
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

class ChatListViewModel(
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository
) : ViewModelBase() {
    val chatRoomList: LiveData<List<ChatRoomModel>>
        get() = _chatRoomList
    private val _chatRoomList = MutableLiveData<List<ChatRoomModel>>()
    val message = MutableLiveData<String>()
    private var subscribeJob: Job? = null

    fun connectChatSever() {
        viewModelScope.launch {
            try {
                chatRepository.connectToChatSever()
                subscribe()
            } catch (e: Exception) {
            }
        }
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
                    getChatRoom()
                }
            }
        }
    }

    fun disConnectChatServer() {
        viewModelScope.launch {
            subscribeJob?.cancelChildren()
            chatRepository.disconnectChatServer()
        }
    }

    fun getChatRoom() {
        viewModelScope.launch {
            try {
                val chatRoom = retry { chatRepository.getChatRooms() }
                val chatRoomModel = withContext(Dispatchers.IO) {
                    chatRoom.toChatRoomModels()
                }
                _chatRoomList.value = chatRoomModel
            } catch (e: HttpException) {

            } catch (e: Exception) {

            }
        }
    }
}
