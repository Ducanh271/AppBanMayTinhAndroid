package com.example.myapplication.data.models

data class ApiResponse(
    val success: Boolean,           // Trạng thái thành công/thất bại
    val message: String,            // Thông báo từ backend
    val cart: Cart?                 // Dữ liệu giỏ hàng (có thể null nếu không có giỏ hàng)
)