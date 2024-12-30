package com.example.myapplication.data.models

data class CreateOrderRequest(
    val userId: String,
    val items: List<OrderItemRequest>,
    val address: String,
    val phoneNumber: String
)

data class OrderItemRequest(
    val productId: String,
    val quantity: Int
)