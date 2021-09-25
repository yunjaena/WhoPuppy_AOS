package com.dicelab.whopuppy.data.entity

data class ChatRoomCreateResponse(
    val articleId: Int? = 0,
    val createUserId: Int? = 0,
    val createdAt: String? = "",
    val id: Long = 0,
    val isDeleted: Boolean? = false,
    val memberCount: Int? = 0,
    val name: String? = "",
    val updatedAt: String? = "",
    val users: List<User> = listOf()
)
