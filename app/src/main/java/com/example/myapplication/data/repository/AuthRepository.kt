package com.example.myapplication.data.repository

import com.example.myapplication.data.api.UserApi
import com.example.myapplication.data.models.LoginRequest
import com.example.myapplication.data.models.SignUpRequest
import com.example.myapplication.data.models.User
import com.google.gson.Gson
import retrofit2.HttpException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log

class AuthRepository(private val userApi: UserApi) {

    // Đăng nhập
    suspend fun login(email: String, password: String): User = withContext(Dispatchers.IO) {
        val response = userApi.login(LoginRequest(email, password))

        return@withContext if (response.isSuccessful) {
            val loginResponse = response.body()
            if (loginResponse != null && loginResponse.user != null) {
                loginResponse.user // Trả về đối tượng User
            } else {
                throw Exception("Invalid login response: User is null")
            }
        } else {
            val errorBody = response.errorBody()?.string()
            val errorJson = try {
                Gson().fromJson(errorBody, ErrorResponse::class.java)
            } catch (e: Exception) {
                null
            }
            throw HttpException(response)
        }
    }

    // Đăng ký
    suspend fun signUp(name: String, email: String, password: String): User = withContext(Dispatchers.IO) {
        val response = userApi.signUp(SignUpRequest(name, email, password))

        return@withContext if (response.isSuccessful) {
            val signUpResponse = response.body()
            if (signUpResponse != null && signUpResponse.userId != null) {
                getUserDetails(signUpResponse.userId)
            } else {
                throw Exception("Invalid sign-up response: UserId is null")
            }
        } else {
            val errorBody = response.errorBody()?.string()
            val errorJson = try {
                Gson().fromJson(errorBody, ErrorResponse::class.java)
            } catch (e: Exception) {
                null
            }
            val errorMessage = errorJson?.message ?: "Sign-up failed"
            throw Exception(errorMessage)
        }
    }
    // Lấy thông tin chi tiết người dùng
    suspend fun getUserDetails(userId: String): User = withContext(Dispatchers.IO) {
        val response = userApi.getUserDetails(userId)

        return@withContext if (response.isSuccessful) {
            val userResponse = response.body()
            if (userResponse != null) {
                // Xử lý nullable trước khi khởi tạo User
                User(
                    id = userResponse.id ?: throw Exception("User ID is missing"),
                    name = userResponse.name ?: "Unknown",
                    email = userResponse.email ?: "Unknown",
                    password = userResponse.password ?: "",
                    createdAt = userResponse.createdAt ?: ""
                )
            } else {
                throw Exception("Invalid user details response: User is null")
            }
        } else {
            val errorBody = response.errorBody()?.string()
            val errorJson = try {
                Gson().fromJson(errorBody, ErrorResponse::class.java)
            } catch (e: Exception) {
                null
            }
            throw Exception(errorJson?.message ?: "Failed to fetch user details")
        }
    }
// Lớp để xử lý lỗi từ API
data class ErrorResponse(val message: String?)}
