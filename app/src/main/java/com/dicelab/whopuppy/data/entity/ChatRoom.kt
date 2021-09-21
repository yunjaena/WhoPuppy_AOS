package com.dicelab.whopuppy.data.entity

import com.dicelab.whopuppy.data.ChatRoomModel
import java.text.SimpleDateFormat
import java.util.*

data class ChatRoom(
    val chatRooms: List<ChatRoomItem> = listOf(),
    val users: List<User> = listOf()
)

fun ChatRoom.toChatRoomModels(): List<ChatRoomModel> {
    return this.chatRooms.map {
        ChatRoomModel(
            roomId = it.id ?: 0,
            articleId = it.articleId ?: 0,
            title = it.name ?: "",
            content = it.chatMessage?.message ?: "",
            updateDate = it.chatMessage?.updatedAt?.getUpdateDate() ?: "",
            memberCount = it.memberCount ?: 0,
            messageCount = it.messageCount ?: 0,
            createUserInfo = this.users.find { user -> user.id == it.createUserId }
        )
    }
}

private fun String.getUpdateDate(): String? {
    val oldFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA)
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
    val timeFormat = SimpleDateFormat("a hh:mm", Locale.KOREA)
    return try {
        val oldDate = oldFormat.parse(this)
        val todayDate = dateFormat.parse(dateFormat.format(Date()))
        return if (dateFormat.parse(this) == todayDate) {
            timeFormat.format(oldDate)
        } else {
            dateFormat.format(oldDate)
        }
    } catch (e: Exception) {
        null
    }
}
