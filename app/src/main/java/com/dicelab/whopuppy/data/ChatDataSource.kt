package com.dicelab.whopuppy.data

import com.dicelab.whopuppy.data.entity.ChatMessage
import com.dicelab.whopuppy.data.entity.ChatRoom
import com.dicelab.whopuppy.data.entity.ChatRoomCreateItem
import com.dicelab.whopuppy.data.entity.ChatRoomCreateResponse
import com.dicelab.whopuppy.data.entity.ChatRoomItem
import kotlinx.coroutines.flow.Flow
import org.hildan.krossbow.stomp.frame.StompFrame

interface ChatDataSource {
    suspend fun connectToChatSever()

    suspend fun subscribeTopic(topic: String): Flow<StompFrame.Message>

    suspend fun sendMessage(topic: String, message: String)

    suspend fun sendMessage(topic: String)

    suspend fun disconnectChatServer()

    suspend fun getChatRooms(): ChatRoom

    suspend fun getChatRoomDetail(roomId: Long): ChatRoomItem

    suspend fun getRecentChatMessage(count: Int, id: Long, roomId: Long): ArrayList<ChatMessage>

    suspend fun createChatRoom(chatRoomCreateItem: ChatRoomCreateItem): Long
}
