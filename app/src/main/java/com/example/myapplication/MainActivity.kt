package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.data.api.ApiService
import com.example.myapplication.data.repository.AuthRepository
import com.example.myapplication.data.repository.ProductRepository
import com.example.myapplication.navigation.AppNavigation
import com.example.myapplication.data.repository.CartRepository
import com.example.myapplication.viewmodel.AuthViewModel
import com.example.myapplication.viewmodel.AuthViewModelFactory
import com.example.myapplication.viewmodel.ProductViewModel
import com.example.myapplication.viewmodel.ProductViewModelFactory
import com.example.myapplication.viewmodel.CartViewModel
import com.example.myapplication.viewmodel.CartViewModelFactory
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val productRepository = ProductRepository(ApiService.productApi)
        val authRepository = AuthRepository(ApiService.userApi)
        val cartRepository = CartRepository(ApiService.cartApi)
        setContent {
            val productViewModel: ProductViewModel = viewModel(
                factory = ProductViewModelFactory(productRepository)
            )

            val authViewModel: AuthViewModel = viewModel(
                factory = AuthViewModelFactory(authRepository)
            )
            val cardViewModel: CartViewModel = viewModel(
                factory = CartViewModelFactory(cartRepository)
            )

            LaunchedEffect(Unit) {
                authViewModel.checkLoginStatus(this@MainActivity)
            }

            val navController = rememberNavController()
            AppNavigation(
                navController = navController,
                viewModel = productViewModel,
                authViewModel = authViewModel,
                cartViewModel = cardViewModel
            )
        }
    }
}