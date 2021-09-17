package com.yunjaena.whopuppy.data

import kotlinx.coroutines.flow.Flow
import org.hildan.krossbow.stomp.frame.StompFrame

class ChatRepository(
    private val chatRemoteDataSource: ChatDataSource
) : ChatDataSource {

    override suspend fun connectToChatSever() {
        chatRemoteDataSource.connectToChatSever()
    }

    override suspend fun subscribeTopic(topic: String): Flow<StompFrame.Message> {
        return chatRemoteDataSource.subscribeTopic(topic)
    }

    override suspend fun sendMessage(topic: String, message: String) {
        chatRemoteDataSource.sendMessage(topic, message)
    }

    override suspend fun sendMessage(topic: String) {
        chatRemoteDataSource.sendMessage(topic)
    }

    override suspend fun disconnectChatServer() {
        chatRemoteDataSource.disconnectChatServer()
    }
}
