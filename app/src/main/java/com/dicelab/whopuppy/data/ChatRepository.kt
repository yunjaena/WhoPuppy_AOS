package com.dicelab.whopuppy.data

import com.dicelab.whopuppy.data.entity.ChatMessage
import com.dicelab.whopuppy.data.entity.ChatRoom
import com.dicelab.whopuppy.data.entity.ChatRoomCreateItem
import com.dicelab.whopuppy.data.entity.ChatRoomItem
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

    override suspend fun getChatRooms(): ChatRoom {
        return chatRemoteDataSource.getChatRooms()
    }

    override suspend fun getChatRoomDetail(roomId: Long): ChatRoomItem {
        return chatRemoteDataSource.getChatRoomDetail(roomId)
    }

    override suspend fun getRecentChatMessage(
        count: Int,
        id: Long,
        roomId: Long
    ): ArrayList<ChatMessage> {
        return chatRemoteDataSource.getRecentChatMessage(count, id, roomId)
    }

    override suspend fun createChatRoom(chatRoomCreateItem: ChatRoomCreateItem): Long {
        return chatRemoteDataSource.createChatRoom(chatRoomCreateItem)
    }
}
