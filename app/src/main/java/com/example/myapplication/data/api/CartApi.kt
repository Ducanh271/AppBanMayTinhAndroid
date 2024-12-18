package com.example.myapplication.data.api

import com.example.myapplication.data.models.CreateCartRequest
import com.example.myapplication.data.models.CreateCartResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface CartApi {
    @POST("/api/carts/create")
    suspend fun createCart(@Body body: HashMap<String, String>): Response<CreateCartResponse>
}