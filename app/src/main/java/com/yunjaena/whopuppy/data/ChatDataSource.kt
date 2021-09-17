package com.yunjaena.whopuppy.data

import kotlinx.coroutines.flow.Flow
import org.hildan.krossbow.stomp.frame.StompFrame

interface ChatDataSource {
    suspend fun connectToChatSever()

    suspend fun subscribeTopic(topic: String): Flow<StompFrame.Message>

    suspend fun sendMessage(topic: String, message: String)

    suspend fun sendMessage(topic: String)

    suspend fun disconnectChatServer()
}
