package com.example.myapplication.data.models

import com.google.gson.annotations.SerializedName

data class OrderResponse(
    @SerializedName("_id") val id: String, // `_id` từ backend
    val userId: String,
    val items: List<OrderItemResponse>,
    val address: String,
    val phoneNumber: String,
    val status: String,
    val createdAt: String,
    val updatedAt: String,
    val total: Double // Tổng giá trị đơn hàng
)

data class OrderItemResponse(
    val productId: String,
    val title: String, // Tên sản phẩm từ backend
    val image: String,
    val quantity: Int, // Số lượng
    val price: Double, // Giá sản phẩm
    val total: Double  // Tổng giá (price * quantity)
)