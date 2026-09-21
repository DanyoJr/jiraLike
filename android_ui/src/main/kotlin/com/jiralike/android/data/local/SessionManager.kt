package com.jiralike.android.data.local

import android.content.Context
import android.content.SharedPreferences

object SessionManager {
    private const val PREF_NAME = "jiralike_prefs"
    private const val KEY_TOKEN = "jwt_token"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveToken(context: Context, token: String) {
        getPrefs(context).edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(context: Context): String? {
        return getPrefs(context).getString(KEY_TOKEN, null)
    }

    fun clear(context: Context) {
        getPrefs(context).edit().clear().apply()
    }
}
