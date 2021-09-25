package com.dicelab.whopuppy.data.entity

data class ChatRoomItem(
    val articleId: Long? = 0,
    val chatMessage: ChatMessage? = ChatMessage(),
    val article: ArticleItem? = ArticleItem(),
    val createUserId: Long? = 0,
    val createdAt: String? = "",
    val id: Long? = 0,
    val isDeleted: Boolean? = false,
    val memberCount: Int? = 0,
    val messageCount: Int? = 0,
    val name: String? = "",
    val updatedAt: String? = ""
)
