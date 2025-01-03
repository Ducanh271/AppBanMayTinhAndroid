package com.example.myapplication.data.models
import com.google.gson.annotations.SerializedName

data class UserResponse(
    val message: String?,
    val user: User? // Trường user chứa thông tin người dùng
)

data class User(
    @SerializedName("_id") val id: String?, // Cho phép `id` là nullable
    val name: String?,
    val email: String?,
    val password: String?,
    val createdAt: String?
)
data class SignUpResponse(
    val message: String,
    val userId: String // Trả về userId khi đăng ký thành công
)
