package com.example.myapplication.data.repository

import com.example.myapplication.data.api.UserApi
import com.example.myapplication.data.models.LoginRequest
import com.example.myapplication.data.models.UserResponse

class AuthRepository(private val userApi: UserApi) {
    suspend fun login(email: String, password: String): String {
        val response = userApi.login(LoginRequest(email, password))
        if (response.isSuccessful && response.body() != null) {
            val userId = response.body()!!.user.id // Lấy user.id từ response
            return userId
        } else {
            throw Exception("Login failed: ${response.message()}")
        }
    }

}
