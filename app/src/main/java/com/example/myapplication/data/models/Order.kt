package com.example.myapplication.data.models

data class OrderResponse(
    val id: String, // `_id` từ backend
    val userId: String,
    val items: List<OrderItemResponse>,
    val address: String,
    val phoneNumber: String,
    val status: String,
    val createdAt: String,
    val updatedAt: String,
    val totalPrice: Double // Tổng giá trị đơn hàng
)
data class DetailOrderResponse(
    val id: String, // `_id` từ backend
    val userId: String,
    val items: List<OrderItemResponse>,
    val address: String,
    val phoneNumber: String,
    val status: String,
    val createdAt: String,
    val updatedAt: String,
    val totalPrice: Double // Tổng giá trị đơn hàng
)

data class OrderItemResponse(
    val productId: String,
    val title: String, // Tên sản phẩm từ backend
    val price: Double, // Giá sản phẩm
    val quantity: Int, // Số lượng
    val total: Double  // Tổng giá (price * quantity)
)