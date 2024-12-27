
package com.example.myapplication.data.models

import com.google.gson.annotations.SerializedName

data class CartItem(
    val productId: String,
    val title: String,
    val price: Double,
    val image: String,
    val quantity: Int,
    val total: Double
)