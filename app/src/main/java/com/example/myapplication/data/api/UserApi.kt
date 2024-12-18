package com.example.myapplication.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import com.example.myapplication.data.models.LoginRequest
import com.example.myapplication.data.models.UserResponse

interface UserApi {
    @POST("api/users/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<UserResponse>
}
