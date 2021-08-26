package com.sarpex.producthunt.network

import android.content.Context
import android.content.SharedPreferences
import com.sarpex.producthunt.R

/**
 * Session manager to save and fetch data from [SharedPreferences].
 */
class SessionManager(context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "user_token"
    }

    /**
     * Save auth token.
     */
    fun saveAuthToken(token: String) {
        with(prefs.edit()) {
            putString(USER_TOKEN, token)
            commit()
        }
    }

    /**
     * Fetch auth token.
     */
    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    /**
     * Clear auth token from shared preferences.
     */
    fun clearAuthToken() {
        prefs.edit().remove(USER_TOKEN).apply()
    }
}
