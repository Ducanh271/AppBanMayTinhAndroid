package com.example.myapplication.utils

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.myapplication.data.models.SavedAccount

class AccountPreferences(context: Context) {
    // Khởi tạo master key cho mã hóa
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    // Khởi tạo EncryptedSharedPreferences để lưu trữ an toàn
    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "encrypted_saved_accounts",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    private val gson = Gson()

    // Kiểm tra tài khoản đã được lưu chưa
    fun isAccountSaved(email: String): Boolean {
        return getSavedAccounts().any { it.email == email }
    }

    // Lưu tài khoản mới
    fun saveAccount(email: String, password: String) {
        val accounts = getSavedAccounts().toMutableList()
        if (!accounts.any { it.email == email }) {
            accounts.add(SavedAccount(email, password))
            val accountsJson = gson.toJson(accounts)
            sharedPreferences.edit().putString("accounts", accountsJson).apply()
        }
    }

    // Lấy danh sách tài khoản đã lưu
    fun getSavedAccounts(): List<SavedAccount> {
        val accountsJson = sharedPreferences.getString("accounts", "[]")
        val type = object : TypeToken<List<SavedAccount>>() {}.type
        return gson.fromJson(accountsJson, type) ?: emptyList()
    }

    // Lấy danh sách gợi ý dựa trên query
    fun getSuggestedAccounts(query: String): List<SavedAccount> {
        return getSavedAccounts().filter {
            it.email.contains(query, ignoreCase = true)
        }
    }

    // Xóa một tài khoản
    fun removeAccount(email: String) {
        val accounts = getSavedAccounts().toMutableList()
        accounts.removeAll { it.email == email }
        val accountsJson = gson.toJson(accounts)
        sharedPreferences.edit().putString("accounts", accountsJson).apply()
    }

    // Xóa tất cả tài khoản đã lưu
    fun clearAllAccounts() {
        sharedPreferences.edit().clear().apply()
    }
}