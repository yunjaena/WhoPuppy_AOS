package com.dicelab.whopuppy.data.entity

import com.google.gson.annotations.SerializedName

data class AuthNumber(
    @SerializedName("account") val account: String? = null,
    @SerializedName("code") val code: String? = null,
    @SerializedName("flag") val flag: Int? = null,
    @SerializedName("phone_number") val phoneNumber: String? = null,
    @SerializedName("secret") val secret: String? = null,
)
