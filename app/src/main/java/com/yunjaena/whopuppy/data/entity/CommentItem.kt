package com.yunjaena.whopuppy.data.entity

import com.google.gson.annotations.SerializedName

data class CommentItem(
    @SerializedName("article_id") val articleId: Long? = null,
    val content: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    val id: Long? = null,
    @SerializedName("is_deleted") val isDeleted: Boolean? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,
    @SerializedName("user_id") val userId: Int? = null,
    @SerializedName("user_nickname") val userNickname: String? = null
)
