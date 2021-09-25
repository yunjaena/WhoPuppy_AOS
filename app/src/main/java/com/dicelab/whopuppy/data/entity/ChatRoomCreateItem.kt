package com.dicelab.whopuppy.data.entity

data class ChatRoomCreateItem(
    val articleId: Long? = 0,
    val name: String? = "",
    val users: List<User> = listOf()
)
