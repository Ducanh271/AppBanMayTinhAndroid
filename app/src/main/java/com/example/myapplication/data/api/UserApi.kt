package com.example.myapplication.data.api

import com.example.myapplication.data.models.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApi {

    @POST("/api/users/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<UserResponse>

    @POST("/api/users")
    suspend fun signUp(@Body signUpRequest: SignUpRequest): Response<UserResponse>
}
