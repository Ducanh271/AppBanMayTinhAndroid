package com.example.myapplication.data.models

data class OrderDetails(
    val orderId: String,
    val userId: String,
    val items: List<OrderItem>,
    val totalAmount: Double,
    val status: String,
    val address: String,
    val phoneNumber: String,
    val createdAt: String
)
