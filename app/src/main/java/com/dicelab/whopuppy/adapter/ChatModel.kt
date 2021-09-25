package com.dicelab.whopuppy.adapter

import com.dicelab.whopuppy.data.entity.User
import kotlin.reflect.KClass

sealed class ChatModel(val id: Long)

data class MyChatText(
    val chatId: Long,
    val message: String,
    val createdAt: String,
    val user: User
) :
    ChatModel(chatId)

data class OpponentText(
    val chatId: Long,
    val message: String,
    val createdAt: String,
    val user: User
) :
    ChatModel(chatId)

fun ChatModel.getViewType(): Int {
    return when (this) {
        is MyChatText -> MY_CHAT_TEXT_VIEW_TYPE
        is OpponentText -> OPPONENT_CHAT_TEXT_VIEW_TYPE
    }
}

fun getChatModelClass(viewType: Int): KClass<out ChatModel> {
    return when (viewType) {
        MY_CHAT_TEXT_VIEW_TYPE -> MyChatText::class
        OPPONENT_CHAT_TEXT_VIEW_TYPE -> OpponentText::class
        else -> throw IllegalArgumentException("viewType unsupported")
    }
}

const val MY_CHAT_TEXT_VIEW_TYPE = 0
const val OPPONENT_CHAT_TEXT_VIEW_TYPE = 1
