package com.example.myapplication.utils

import android.content.Context

object SharedPrefUtils {
    private const val PREF_NAME = "app_prefs"
    private const val KEY_USER_ID = "userId"
    private const val KEY_USER_NAME = "userName"
    private const val KEY_USER_EMAIL = "userEmail"

    // Lưu User ID

    fun saveUserId(context: Context, userId: String?) {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(KEY_USER_ID, userId ?: "Unknown ID")
            apply()
        }
    }

    // Lấy User ID
    fun getUserId(context: Context): String? {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPref.getString(KEY_USER_ID, null)
    }

    // Lưu tên người dùng
    fun saveUserName(context: Context, name: String?) {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(KEY_USER_NAME, name ?: "Unknown Name")
            apply()
        }
    }

    // Lấy tên người dùng
    fun getUserName(context: Context): String? {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPref.getString(KEY_USER_NAME, null)
    }

    // Lưu email
    fun saveUserEmail(context: Context, email: String?) {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(KEY_USER_EMAIL, email ?: "Unknown Email")
            apply()
        }
    }

    // Lấy email
    fun getUserEmail(context: Context): String? {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPref.getString(KEY_USER_EMAIL, null)
    }
    // Xóa tất cả thông tin người dùng
    fun clearAllUserData(context: Context) {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            clear()
            apply()
        }
    }

}
