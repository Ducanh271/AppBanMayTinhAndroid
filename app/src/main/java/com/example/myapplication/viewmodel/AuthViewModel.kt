package com.example.myapplication.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.api.ApiService
import com.example.myapplication.data.repository.AuthRepository
import com.example.myapplication.utils.SharedPrefUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SignUpState(
    val isLoading: Boolean = false,
    val success: String? = null,
    val error: String? = null
)

data class LoginState(
    val isLoading: Boolean = false,
    val success: String? = null,
    val error: String? = null
)

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _signUpState = MutableStateFlow(SignUpState())
    val signUpState: StateFlow<SignUpState> = _signUpState

    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private suspend fun createCartForUser(userId: String) {
        try {
            val body = HashMap<String, String>()
            body["userId"] = userId
            val response = ApiService.cartApi.createCart(body)
            if (!response.isSuccessful) {
                throw Exception("Failed to create cart")
            }
        } catch (e: Exception) {
            throw e
        }
    }

    fun signUp(name: String, email: String, password: String, context: Context) {
        viewModelScope.launch {
            try {
                _signUpState.value = SignUpState(isLoading = true)

                // 1. Đăng ký người dùng
                val userId = repository.signUp(name, email, password)
                SharedPrefUtils.saveUserId(context, userId)

                // 2. Tạo giỏ hàng cho người dùng mới
                try {
                    createCartForUser(userId)
                    _signUpState.value = SignUpState(success = userId)
                    Toast.makeText(context, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Log.e("Cart", "Failed to create cart: ${e.message}")
                    // Nếu tạo giỏ hàng thất bại, vẫn giữ tài khoản nhưng thông báo lỗi
                    _signUpState.value = SignUpState(success = userId)
                    Toast.makeText(context, "Khởi tao gior hàng thất bại", Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                Log.e("SignUp", "Error during signup: ${e.message}")
                _signUpState.value = SignUpState(error = e.message ?: "Unknown error occurred")
            }
        }
    }

    fun login(email: String, password: String, context: Context) {
        viewModelScope.launch {
            _loginState.value = LoginState(isLoading = true)
            try {
                val userId = repository.login(email, password)
                SharedPrefUtils.saveUserId(context, userId)
                _loginState.value = LoginState(success = userId)
                _isLoggedIn.value = true
            } catch (e: Exception) {
                _loginState.value = LoginState(error = e.message)
            }
        }
    }

    fun checkLoginStatus(context: Context) {
        val userId = SharedPrefUtils.getUserId(context)
        _isLoggedIn.value = !userId.isNullOrEmpty()
    }

    fun logout(context: Context) {
        SharedPrefUtils.clearUserId(context)
        _isLoggedIn.value = false
        _loginState.value = LoginState()
    }
}
