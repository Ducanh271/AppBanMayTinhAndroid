package com.example.myapplication.data.models


// Request cho đăng ký
data class SignUpRequest(
    val name: String,
    val email: String,
    val password: String,
)

