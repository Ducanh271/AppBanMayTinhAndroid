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

    // Hàm đăng ký
    fun signUp(name: String, email: String, password: String, context: Context) {
        viewModelScope.launch {
            try {
                _signUpState.value = SignUpState(isLoading = true)

                // Đăng ký và lấy thông tin người dùng chi tiết
                val user = repository.signUp(name, email, password)

                val userId = user.id ?: "Unknown ID"
                // Tạo giỏ hàng cho người dùng
                try {
                    createCartForUser(userId)
                    _signUpState.value = SignUpState(success = userId)
                    Toast.makeText(context, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Log.e("Cart", "Failed to create cart: ${e.message}")
                    _signUpState.value = SignUpState(success = userId)
                    Toast.makeText(context, "Khởi tạo giỏ hàng thất bại", Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                Log.e("SignUp", "Error during signup: ${e.message}")
                _signUpState.value = SignUpState(error = e.message ?: "Unknown error occurred")
            }
        }
    }

    // Hàm đăng nhập
    fun login(email: String, password: String, context: Context) {
        viewModelScope.launch {
            _loginState.value = LoginState(isLoading = true)
            try {
                // Gọi repository để đăng nhập
                val user = repository.login(email, password)

                // Lưu thông tin người dùng vào SharedPreferences
                val userId = user.id ?: "Unknown ID"
                val userName = user.name ?: "Unknown Name"
                val userEmail = user.email ?: "Unknown Email"

                SharedPrefUtils.saveUserId(context, userId)
                SharedPrefUtils.saveUserName(context, userName)
                SharedPrefUtils.saveUserEmail(context, userEmail)

                _loginState.value = LoginState(success = userId)
                _isLoggedIn.value = true
            } catch (e: Exception) {
                _loginState.value = LoginState(error = e.message)
            }
        }
    }

    // Kiểm tra trạng thái đăng nhập
    fun checkLoginStatus(context: Context) {
        val userId = SharedPrefUtils.getUserId(context)
        _isLoggedIn.value = !userId.isNullOrEmpty()
    }

    // Hàm đăng xuất
    fun logout(context: Context) {
        SharedPrefUtils.clearAllUserData(context)
        _isLoggedIn.value = false
        _loginState.value = LoginState()
    }
}
