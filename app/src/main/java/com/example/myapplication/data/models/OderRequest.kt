package com.example.myapplication.data.models

data class OrderRequest(
    val userId: String,
    val items: List<OrderItem>,
    val address: String,
    val phoneNumber: String
)

data class OrderItem(
    val productId: String,
    val quantity: Int
)