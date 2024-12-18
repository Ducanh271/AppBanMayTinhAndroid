package com.example.myapplication.utils

import android.content.Context

object SharedPrefUtils {
    private const val PREF_NAME = "app_prefs"
    private const val KEY_USER_ID = "userId"

    // Hàm lưu userId vào SharedPreferences
    fun saveUserId(context: Context, userId: String) {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(KEY_USER_ID, userId)
            apply()
        }
    }

    // Hàm lấy userId từ SharedPreferences
    fun getUserId(context: Context): String? {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPref.getString(KEY_USER_ID, null)
    }

    // Hàm xóa userId (ví dụ: khi đăng xuất)
    fun clearUserId(context: Context) {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            remove(KEY_USER_ID)
            apply()
        }
    }
}
