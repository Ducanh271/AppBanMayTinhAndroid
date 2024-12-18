package com.example.myapplication.data.models

// Import Product model
import com.example.myapplication.data.models.Product

data class ProductResponse(
    val products: List<Product> // Sử dụng Product model trong danh sách
)