package com.dicelab.whopuppy.data.entity

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

data class CommentItem(
    @SerializedName("article_id") val articleId: Long? = null,
    val content: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    val id: Long? = null,
    @SerializedName("is_deleted") val isDeleted: Boolean? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,
    @SerializedName("user_id") val userId: Int? = null,
    @SerializedName("user_nickname") val userNickname: String? = null,
    @SerializedName("user_profile_image") val userProfileImage: String? = null
) {
    fun getUpdateDate(): String? {
        if (updatedAt == null) return null
        val oldFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZ", Locale.KOREA)
        oldFormat.timeZone = TimeZone.getTimeZone("KST")
        val newFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA)
        return try {
            val oldDate = oldFormat.parse(updatedAt)
            newFormat.format(oldDate)
        } catch (e: Exception) {
            null
        }
    }
}
