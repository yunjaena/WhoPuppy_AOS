package com.dicelab.whopuppy.data.entity

import com.dicelab.whopuppy.adapter.ChatModel
import com.dicelab.whopuppy.adapter.MyChatText
import com.dicelab.whopuppy.adapter.OpponentText
import java.text.SimpleDateFormat
import java.util.*

data class ChatMessage(
    val chatRoomId: Long? = 0,
    val createdAt: String? = "",
    val id: Long? = 0,
    val isDeleted: Boolean? = false,
    val message: String? = "",
    val readCount: Int? = 0,
    val sendUserId: Int? = 0,
    val user: User? = null,
    val updatedAt: String? = ""
)

fun List<ChatMessage>.toChatModelList(myId: Long): List<ChatModel> {
    return this.map {
        if (it.user?.id == myId) {
            MyChatText(it.id!!, it.message ?: "", it.createdAt?.formatDate() ?: "", it.user)
        } else {
            OpponentText(it.id!!, it.message ?: "", it.createdAt?.formatDate() ?: "", it.user!!)
        }
    }
}

private fun String.formatDate(): String? {
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
