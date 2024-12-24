package com.example.myapplication.data.api


import com.example.myapplication.data.models.AddToCartRequest
import com.example.myapplication.data.models.Cart
import com.example.myapplication.data.models.CreateCartResponse
import com.example.myapplication.data.models.UpdateCartItemRequest
import retrofit2.Response
import retrofit2.http.*

interface CartApi {
    @POST("/api/carts/create")
    suspend fun createCart(@Body body: HashMap<String, String>): Response<CreateCartResponse>

    @GET("/api/carts/{userId}")
    suspend fun getCartByUserId(@Path("userId") userId: String): Response<Cart>

    @POST("/api/carts/add")
    suspend fun addToCart(@Body request: AddToCartRequest): Response<Cart>

    @PUT("/api/carts/{userId}/update-item/{productId}")
    suspend fun updateCartItem(
        @Path("userId") userId: String,
        @Path("productId") productId: String,
        @Body request: UpdateCartItemRequest
    ): Response<Cart>

    @DELETE("/api/carts/{userId}/remove-item/{productId}")
    suspend fun removeCartItem(
        @Path("userId") userId: String,
        @Path("productId") productId: String
    ): Response<Unit>

    @DELETE("/api/carts/{userId}/clear")
    suspend fun clearCart(@Path("userId") userId: String): Response<Unit>

    @GET("/api/carts")
    suspend fun getAllCarts(): Response<List<Cart>>
}