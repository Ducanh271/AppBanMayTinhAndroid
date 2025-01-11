package com.example.myapplication.data.models

data class CreateOrderResponse(
    val orderId: String,
    val message: String,
    val total: Double // Thêm trường tổng giá trị đơn hàng
)