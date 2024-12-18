package com.example.myapplication.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.ui.screens.LoginScreen
import com.example.myapplication.ui.screens.MainScreen
import com.example.myapplication.ui.screens.ProductDetailScreen
import com.example.myapplication.utils.SharedPrefUtils
import com.example.myapplication.viewmodel.AuthViewModel
import com.example.myapplication.viewmodel.ProductViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    viewModel: ProductViewModel,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    val isLoggedIn = remember { mutableStateOf(false) }

    // Kiểm tra userId từ SharedPreferences
    LaunchedEffect(Unit) {
        val userId = SharedPrefUtils.getUserId(context)
        println("Debug: Retrieved userId from SharedPreferences: $userId") // Log kiểm tra
        isLoggedIn.value = !userId.isNullOrEmpty() // Kiểm tra kỹ giá trị userId
    }

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn.value) "product_list" else "login"
    ) {
        // Màn hình đăng nhập
        composable("login") {
            LoginScreen(viewModel = authViewModel) {
                navController.navigate("product_list") {
                    popUpTo("login") { inclusive = true }
                }
            }
        }

        // Màn hình danh sách sản phẩm
        composable("product_list") {
            MainScreen(
                viewModel = viewModel,
                navController = navController, // Truyền NavController
                onProductClick = { product ->
                    // Điều hướng sang màn hình chi tiết sản phẩm
                    navController.navigate("product_detail/${product.id}")
                }
            )
        }
        // Màn hình chi tiết sản phẩm
        composable("product_detail/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: return@composable
            LaunchedEffect(productId) { viewModel.fetchProductById(productId) }

            val product by viewModel.selectedProduct.collectAsState()
            Box(modifier = Modifier.fillMaxSize()) {
                if (product != null) {
                    ProductDetailScreen(
                        product = product!!,
                        onBack = { navController.popBackStack() }
                    )
                } else {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}
