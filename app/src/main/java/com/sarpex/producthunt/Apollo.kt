package com.sarpex.producthunt

import android.content.Context
import android.os.Looper
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.cache.http.HttpCachePolicy
import com.sarpex.producthunt.network.LoginClient
import com.sarpex.producthunt.network.LoginRequest
import com.sarpex.producthunt.network.SessionManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import java.lang.IllegalStateException

private var instance: ApolloClient? = null

fun apolloClient(context: Context): ApolloClient {
    check(Looper.myLooper() == Looper.getMainLooper()) {
        "Only the main thread can get the apolloClient instance"
    }

    if (instance != null) {
        return instance!!
    }

    val sessionManager = SessionManager(context)

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthorizationInterceptor(context, sessionManager))
        .build()

    instance = ApolloClient.builder()
        .serverUrl("https://api.producthunt.com/v2/api/graphql")
        .okHttpClient(okHttpClient)
        .defaultHttpCachePolicy(HttpCachePolicy.CACHE_FIRST)
        .build()

    return instance!!
}

private class AuthorizationInterceptor(val context: Context, val sessionManager: SessionManager) :
    Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var authToken = sessionManager.fetchAuthToken()
        if (authToken == null) {
            val loginResponse = LoginClient().getLoginService().login(
                LoginRequest(
                    clientId = CLIENT_ID,
                    clientSecret = CLIENT_SECRET,
                    grantType = GRANT_TYPE
                )
            ).execute()
            if (!loginResponse.isSuccessful) {
                throw IllegalStateException("Credential error.")
            }
            authToken = loginResponse.body()?.accessToken ?: throw IllegalStateException("Null authentication token.")
            sessionManager.saveAuthToken(authToken)
        }

        // CO-2uy9MB2Nd0HucTcGDI2hb4YcJGz89ps8Qi-5_vM4
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $authToken")
            .build()

        val response = chain.proceed(request)
        // Improve this part with proper error code.
        if (!response.isSuccessful) {
            sessionManager.clearAuthToken()
        }
        return response
    }

    companion object {
        // Check OAuth section in the main README file https://github.com/simoarpe/product-hunt#-oauth.
        private val CLIENT_ID = "YOUR_API_KEY_GOES_HERE"
        private val CLIENT_SECRET = "YOUR_API_SECRET_GOES_HERE"
        private val GRANT_TYPE = "client_credentials"
    }
}
