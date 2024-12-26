// CartRepository.kt
package com.example.myapplication.data.repository

import com.example.myapplication.data.api.CartApi
import com.example.myapplication.data.models.*
import retrofit2.Response

class CartRepository(private val cartApi: CartApi) {

    suspend fun createCart(userId: String): Response<CreateCartResponse> {
        return cartApi.createCart(hashMapOf("userId" to userId))
    }

    suspend fun getCart(userId: String): Cart {
        return try {
            cartApi.getCart(userId)
        } catch (e: Exception) {
            throw Exception("Error fetching cart: ${e.message}")
        }
    }

    suspend fun addToCart(request: AddToCartRequest): ApiResponse {
        return try {
            cartApi.addToCart(request)
        } catch (e: Exception) {
            throw Exception("Error adding to cart: ${e.message}")
        }
    }

    suspend fun updateCartItem(userId: String, productId: String, request: UpdateCartItemRequest): ApiResponse {
        return try {
            cartApi.updateCartItem(userId, productId, request)
        } catch (e: Exception) {
            throw Exception("Error updating cart item: ${e.message}")
        }
    }

    suspend fun removeCartItem(userId: String, productId: String): ApiResponse {
        return try {
            cartApi.removeCartItem(userId, productId)
        } catch (e: Exception) {
            throw Exception("Error removing cart item: ${e.message}")
        }
    }

    suspend fun clearCart(userId: String): ApiResponse {
        return try {
            cartApi.clearCart(userId)
        } catch (e: Exception) {
            throw Exception("Error clearing cart: ${e.message}")
        }
    }
}
