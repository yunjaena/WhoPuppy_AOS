package com.yunjaena.whopuppy.data.entity

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("account") val account: String? = null,
    @SerializedName("nickname") val nickname: String? = null,
    @SerializedName("password") val password: String? = null,
    @SerializedName("phone_number") val phoneNumber: String? = null
)
