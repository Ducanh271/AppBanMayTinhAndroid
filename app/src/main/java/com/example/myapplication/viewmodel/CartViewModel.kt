package com.example.myapplication.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.models.AddToCartRequest
import com.example.myapplication.data.models.Cart
import com.example.myapplication.data.models.CartItem
import com.example.myapplication.data.repository.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CartViewModel(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _cart = MutableStateFlow<Cart?>(null)
    val cart: StateFlow<Cart?> = _cart

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun getCart(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = cartRepository.getCartByUserId(userId)
                if (response.isSuccessful) {
                    _cart.value = response.body()
                } else {
                    _errorMessage.value = "Failed to get cart: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error getting cart: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addToCart(userId: String, productId: String, quantity: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = cartRepository.addToCart(AddToCartRequest(userId, productId, quantity))
                if (response.isSuccessful) {
                    _cart.value = response.body()
                    // Cập nhật lại giỏ hàng sau khi thêm thành công
                    getCart(userId)
                } else {
                    _errorMessage.value = "Failed to add to cart: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error adding to cart: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateQuantity(userId: String, productId: String, quantity: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = cartRepository.updateCartItem(userId, productId, quantity)
                if (response.isSuccessful) {
                    _cart.value = response.body()
                    // Cập nhật lại giỏ hàng sau khi sửa số lượng thành công
                    getCart(userId)
                } else {
                    _errorMessage.value = "Failed to update quantity: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error updating quantity: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun removeItem(userId: String, productId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = cartRepository.removeCartItem(userId, productId)
                if (response.isSuccessful) {
                    // Cập nhật lại giỏ hàng sau khi xóa thành công
                    getCart(userId)
                } else {
                    _errorMessage.value = "Failed to remove item: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error removing item: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearCart(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = cartRepository.clearCart(userId)
                if (response.isSuccessful) {
                    _cart.value = null // Reset giỏ hàng
                } else {
                    _errorMessage.value = "Failed to clear cart: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error clearing cart: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}