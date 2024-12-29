package com.example.myapplication.data.models

data class Order(
    val orderId: String,
    val userId: String,
    val items: List<OrderItem>,
    val totalAmount: Double,
    val status: String,
    val createdAt: String
)
