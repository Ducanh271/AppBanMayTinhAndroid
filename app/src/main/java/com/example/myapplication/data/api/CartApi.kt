package com.example.myapplication.data.api

import com.example.myapplication.data.models.Cart
import retrofit2.http.*
import com.example.myapplication.data.models.AddToCartRequest
import com.example.myapplication.data.models.UpdateCartItemRequest
import com.example.myapplication.data.models.ApiResponse
import com.example.myapplication.data.models.CreateCartResponse
import retrofit2.Response

interface CartApi {
    @POST("/api/carts/create")
    suspend fun createCart(@Body body: HashMap<String, String   >): Response<CreateCartResponse>

    @GET("/api/carts/{userId}")
    suspend fun getCart(@Path("userId") userId: String): Cart

    @POST("/api/carts/add")
    suspend fun addToCart(
        @Body request: AddToCartRequest
    ): ApiResponse

    @PUT("/api/carts/{userId}/update-item/{productId}")
    suspend fun updateCartItem(
        @Path("userId") userId: String,
        @Path("productId") productId: String,
        @Body request: UpdateCartItemRequest
    ): ApiResponse

    @DELETE("/api/carts/{userId}/remove-item/{productId}")
    suspend fun removeCartItem(
        @Path("userId") userId: String,
        @Path("productId") productId: String
    ): ApiResponse

    @DELETE("/api/carts/{userId}/clear")
    suspend fun clearCart(@Path("userId") userId: String): ApiResponse

}
