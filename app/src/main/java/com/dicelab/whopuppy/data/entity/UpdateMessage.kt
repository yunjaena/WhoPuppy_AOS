package com.dicelab.whopuppy.data.entity

import com.google.gson.Gson

data class UpdateMessage(
    val chatRoomId: Long = 0,
    val createdAt: String? = "",
    val id: Int? = 0,
    val isDeleted: Boolean? = false,
    val message: String? = "",
    val readCount: Int? = 0,
    val sendUserId: Int? = 0,
    val updatedAt: String = "",
    val user: User? = User()
)

fun String.toUpdateMessage(): UpdateMessage {
    return Gson().fromJson(this, UpdateMessage::class.java)
}
