package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repository.AuthRepository
import com.example.myapplication.utils.SharedPrefUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.content.Context

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState

    fun login(email: String, password: String, context: Context) {
        viewModelScope.launch {
            _loginState.value = LoginState(isLoading = true)
            try {
                val userId = repository.login(email, password) // Lấy userId từ AuthRepository
                SharedPrefUtils.saveUserId(context, userId) // Lưu userId
                _loginState.value = LoginState(success = userId)
            } catch (e: Exception) {
                _loginState.value = LoginState(error = e.message)
            }
        }
    }

}

data class LoginState(
    val isLoading: Boolean = false,
    val success: String? = null,
    val error: String? = null
)
