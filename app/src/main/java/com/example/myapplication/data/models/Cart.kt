package com.example.myapplication.data.models

data class Cart(
    val _id: String,
    val items: List<CartItem>,
    val createdAt: String,
    val updatedAt: String
)