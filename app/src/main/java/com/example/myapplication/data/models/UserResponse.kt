package com.example.myapplication.data.models

data class UserResponse(
    val message: String,
    val user: User
)

data class User(
    val id: String,
    val name: String,
    val email: String
)
