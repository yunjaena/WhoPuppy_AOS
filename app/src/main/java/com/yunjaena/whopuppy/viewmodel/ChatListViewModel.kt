package com.yunjaena.whopuppy.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.yunjaena.whopuppy.base.viewmodel.ViewModelBase
import com.yunjaena.whopuppy.data.ChatRepository
import com.yunjaena.whopuppy.data.UserRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ChatListViewModel(
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository
) : ViewModelBase() {
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
                    message.value = it.bodyAsText
                }
            }
        }
    }

    fun sendMessage(chatRoomId: Int, message: String) {
        viewModelScope.launch {
            try {
                val jsonString = Gson().toJson(Message(chatRoomId, message))
                Logger.d("NYJ $jsonString")
                chatRepository.sendMessage("/pub/chat/message", jsonString)
            } catch (e: Exception) {
                Logger.e(e.message ?: "")
            }
        }
    }

    fun readMessage(messageId: Int) {
        viewModelScope.launch {
            try {
                chatRepository.sendMessage("/pub/chat/message/$messageId")
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
}

data class Message(
    val chatRoomId: Int,
    val message: String
)
