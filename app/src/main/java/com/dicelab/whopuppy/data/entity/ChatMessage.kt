package com.dicelab.whopuppy.data.entity

data class ChatMessage(
    val chatRoomId: Int? = 0,
    val createdAt: String? = "",
    val id: Long? = 0,
    val isDeleted: Boolean? = false,
    val message: String? = "",
    val readCount: Int? = 0,
    val sendUserId: Int? = 0,
    val updatedAt: String? = ""
)
