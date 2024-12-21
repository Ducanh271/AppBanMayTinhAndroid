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

// State cho quá trình đăng ký
data class SignUpState(
    val isLoading: Boolean = false,
    val success: String? = null,
    val error: String? = null
)

// State cho quá trình đăng nhập
data class LoginState(
    val isLoading: Boolean = false,
    val success: String? = null,
    val error: String? = null
)

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    // StateFlow cho trạng thái đăng ký
    private val _signUpState = MutableStateFlow(SignUpState())
    val signUpState: StateFlow<SignUpState> = _signUpState

    // StateFlow cho trạng thái đăng nhập
    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState

    // StateFlow cho trạng thái đã đăng nhập
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    // Hàm xử lý đăng nhập với prompt lưu tài khoản
    fun loginWithSavePrompt(
        email: String,
        password: String,
        context: Context,
        onLoginSuccess: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Cập nhật trạng thái loading
                _loginState.value = LoginState(isLoading = true)

                // Thực hiện đăng nhập
                val userId = repository.login(email, password)
                SharedPrefUtils.saveUserId(context, userId)

                // Cập nhật trạng thái sau khi đăng nhập thành công
                _loginState.value = LoginState(success = userId)
                _isLoggedIn.value = true

                // Gọi callback với true để hiển thị dialog
                onLoginSuccess(true)
            } catch (e: Exception) {
                // Xử lý lỗi đăng nhập
                Log.e("Login", "Error during login: ${e.message}")
                _loginState.value = LoginState(error = e.message ?: "Unknown error occurred")
                onLoginSuccess(false)
            }
        }
    }

    // Hàm tạo giỏ hàng cho người dùng mới
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

    // Hàm xử lý đăng ký
    fun signUp(name: String, email: String, password: String, context: Context) {
        viewModelScope.launch {
            try {
                _signUpState.value = SignUpState(isLoading = true)
                val userId = repository.signUp(name, email, password)
                SharedPrefUtils.saveUserId(context, userId)

                try {
                    createCartForUser(userId)
                    _signUpState.value = SignUpState(success = userId)
                    Toast.makeText(context, "Registration and cart creation successful!", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Log.e("Cart", "Failed to create cart: ${e.message}")
                    _signUpState.value = SignUpState(success = userId)
                    Toast.makeText(context, "Account created but failed to create cart", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e("SignUp", "Error during signup: ${e.message}")
                _signUpState.value = SignUpState(error = e.message ?: "Unknown error occurred")
            }
        }
    }

    // Kiểm tra trạng thái đăng nhập
    fun checkLoginStatus(context: Context) {
        val userId = SharedPrefUtils.getUserId(context)
        _isLoggedIn.value = !userId.isNullOrEmpty()
    }

    // Xử lý đăng xuất
    fun logout(context: Context) {
        SharedPrefUtils.clearUserId(context)
        _isLoggedIn.value = false
        _loginState.value = LoginState()
    }
}