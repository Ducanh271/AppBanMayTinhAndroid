package com.example.myapplication.data.api

import com.example.myapplication.data.models.LoginRequest
import com.example.myapplication.data.models.SignUpRequest
import com.example.myapplication.data.models.UserResponse
import com.example.myapplication.data.models.User
import com.example.myapplication.data.models.SignUpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserApi {

    @POST("/api/users/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<UserResponse>

    @POST("/api/users")
    suspend fun signUp(@Body signUpRequest: SignUpRequest): Response<SignUpResponse>

    @GET("/api/users/{userId}")
    suspend fun getUserDetails(@Path("userId") userId: String): Response<User>
}
