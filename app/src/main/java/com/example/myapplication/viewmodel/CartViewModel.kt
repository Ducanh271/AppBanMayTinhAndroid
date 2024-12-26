package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.models.*
import com.example.myapplication.data.repository.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CartViewModel(private val cartRepository: CartRepository) : ViewModel() {

    private val _cartState = MutableStateFlow<Cart?>(null)
    val cartState: StateFlow<Cart?> = _cartState

    private val _message = MutableStateFlow<String>("")
    val message: StateFlow<String> = _message

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    fun loadCart(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _cartState.value = cartRepository.getCart(userId)
            } catch (e: Exception) {
                _message.value = "Failed to load cart: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addToCart(request: AddToCartRequest, onResult: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = cartRepository.addToCart(request) // Gọi API qua repository
                if (response.success) {
                    onResult(response.message ?: "Sản phẩm đã được thêm vào giỏ hàng")
                } else {
                    onResult(response.message ?: "Thêm sản phẩm vào giỏ hàng thất bại")
                }
            } catch (e: Exception) {
                onResult("Đã xảy ra lỗi: ${e.message}") // Xử lý lỗi ngoại lệ
            }
        }
    }
    fun updateCartItem(userId: String, productId: String, request: UpdateCartItemRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = cartRepository.updateCartItem(userId, productId, request)
                _message.value = response.message
                loadCart(userId)
            } catch (e: Exception) {
                _message.value = "Failed to update item: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun removeCartItem(userId: String, productId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = cartRepository.removeCartItem(userId, productId)
                _message.value = response.message
                loadCart(userId)
            } catch (e: Exception) {
                _message.value = "Failed to remove item: ${e.message}"
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
                _message.value = response.message
                loadCart(userId)
            } catch (e: Exception) {
                _message.value = "Failed to clear cart: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
