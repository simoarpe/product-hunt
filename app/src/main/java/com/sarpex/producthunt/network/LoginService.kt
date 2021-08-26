package com.sarpex.producthunt.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Authentication end point.
 */
interface LoginService {
    @POST("v2/oauth/token")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}
