package com.example.myapplication.data.models

import com.google.gson.annotations.SerializedName

data class Cart(
    val items: List<CartItem>,
    val totalPrice: Double,
    val createdAt: String,
    val updatedAt: String
)