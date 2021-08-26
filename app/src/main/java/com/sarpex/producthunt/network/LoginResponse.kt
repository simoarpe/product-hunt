package com.sarpex.producthunt.network

import com.google.gson.annotations.SerializedName

/**
 * Login response data class.
 */
data class LoginResponse(
    @SerializedName("access_token")
    var accessToken: String,

    @SerializedName("token_type")
    var tokenType: String,

    @SerializedName("scope")
    var scope: String,

    @SerializedName("created_at")
    var createdAt: String
)
