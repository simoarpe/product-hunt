package com.sarpex.producthunt.network

import com.google.gson.annotations.SerializedName

/**
 * Login request data class.
 */
data class LoginRequest(
    @SerializedName("client_id")
    var clientId: String,

    @SerializedName("client_secret")
    var clientSecret: String,

    @SerializedName("grant_type")
    var grantType: String
)
