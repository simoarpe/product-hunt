package com.sarpex.producthunt.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Retrofit instance class.
 */
class LoginClient {
    private lateinit var loginService: LoginService

    fun getLoginService(): LoginService {
        if (!::loginService.isInitialized) {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.producthunt.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            loginService = retrofit.create(LoginService::class.java)
        }

        return loginService
    }
}
