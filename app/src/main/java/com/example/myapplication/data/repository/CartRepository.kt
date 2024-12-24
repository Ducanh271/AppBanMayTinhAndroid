package com.example.myapplication.data.repository

import com.example.myapplication.data.api.CartApi
import com.example.myapplication.data.models.AddToCartRequest
import com.example.myapplication.data.models.UpdateCartItemRequest
import retrofit2.Response

class CartRepository (private val cartApi: CartApi) {
    suspend fun getCartByUserId(userId: String) = cartApi.getCartByUserId(userId)
    suspend fun addToCart(request: AddToCartRequest) = cartApi.addToCart(request)
    suspend fun updateCartItem(userId: String, productId: String, quantity: Int) =
        cartApi.updateCartItem(userId, productId, UpdateCartItemRequest(quantity))
    suspend fun removeCartItem(userId: String, productId: String) =
        cartApi.removeCartItem(userId, productId)
    suspend fun clearCart(userId: String) = cartApi.clearCart(userId)
}