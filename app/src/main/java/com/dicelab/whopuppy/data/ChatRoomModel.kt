package com.dicelab.whopuppy.data

import com.dicelab.whopuppy.data.entity.User

data class ChatRoomModel(
    val roomId: Long,
    val articleId: Long,
    val title: String,
    val content: String,
    val updateDate: String,
    val memberCount: Int,
    val messageCount: Int,
    val createUserInfo: User?
)
