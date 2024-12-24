package com.example.myapplication.data.models
data class AddToCartRequest(
    val userId: String,
    val productId: String,
    val quantity: Int
)