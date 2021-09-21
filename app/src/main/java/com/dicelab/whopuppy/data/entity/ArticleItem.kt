package com.dicelab.whopuppy.data.entity

import com.google.gson.annotations.SerializedName

data class ArticleItem(
    val board: String? = null,
    @SerializedName("board_id") val boardId: Long? = null,
    val content: String? = null,
    val createdAt: String? = null,
    val id: Long? = null,
    val images: List<String>? = null,
    val isDeleted: Boolean? = null,
    val nickname: String? = null,
    @SerializedName("profile_image_url") val profileImageUrl: String? = null,
    val region: String? = null,
    val thumbnail: String? = null,
    val title: String? = null,
    val updatedAt: String? = null,
    @SerializedName("user_id") val userId: Long? = null
)
