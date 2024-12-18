package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.data.api.ApiService
import com.example.myapplication.data.repository.AuthRepository
import com.example.myapplication.data.repository.ProductRepository
import com.example.myapplication.navigation.AppNavigation
import com.example.myapplication.viewmodel.AuthViewModel
import com.example.myapplication.viewmodel.AuthViewModelFactory
import com.example.myapplication.viewmodel.ProductViewModel
import com.example.myapplication.viewmodel.ProductViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Khởi tạo Repository
        val productRepository = ProductRepository(ApiService.productApi)
        val authRepository = AuthRepository(ApiService.userApi)

        setContent {
            // Khởi tạo ViewModel cho sản phẩm
            val productViewModel: ProductViewModel = viewModel(
                factory = ProductViewModelFactory(productRepository)
            )

            // Khởi tạo ViewModel cho đăng nhập
            val authViewModel: AuthViewModel = viewModel(
                factory = AuthViewModelFactory(authRepository)
            )

            // Tạo NavController và gọi AppNavigation
            val navController = rememberNavController()
            AppNavigation(
                navController = navController,
                viewModel = productViewModel,
                authViewModel = authViewModel
            )
        }
    }
}
