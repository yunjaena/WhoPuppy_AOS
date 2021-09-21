package com.dicelab.whopuppy.data.entity

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("account") val account: String? = null,
    @SerializedName("nickname") val nickname: String? = null,
    @SerializedName("password") val password: String? = null,
    @SerializedName("phone_number") val phoneNumber: String? = null,
    @SerializedName("secret") val secret: String? = null,
    @SerializedName("profile_image_url") val profileImageUrl: String? = null
)
