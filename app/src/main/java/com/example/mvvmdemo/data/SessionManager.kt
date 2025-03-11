package com.example.mvvmdemo.data

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)

    companion object {
        const val USER_ID = "user_id"
        const val USERNAME = "username"
    }

    fun saveUserSession(userId: Int, username: String) {
        val editor = prefs.edit()
        editor.putInt(USER_ID, userId)
        editor.putString(USERNAME, username)
        editor.apply()
    }

    fun getUserId(): Int = prefs.getInt(USER_ID, -1)

    fun getUsername(): String? = prefs.getString(USERNAME, null)

    fun clearSession() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }

    fun isLoggedIn(): Boolean = prefs.contains(USER_ID) && prefs.contains(USERNAME)
}