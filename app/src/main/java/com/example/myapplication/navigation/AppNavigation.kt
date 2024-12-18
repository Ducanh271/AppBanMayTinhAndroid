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
import com.example.myapplication.ui.screens.SignUpScreen
import com.example.myapplication.viewmodel.AuthViewModel
import com.example.myapplication.viewmodel.ProductViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    viewModel: ProductViewModel,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    LaunchedEffect(Unit) {
        authViewModel.checkLoginStatus(context)
    }

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            navController.navigate("product_list") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) "product_list" else "login"
    ) {
        composable("login") {
            LoginScreen(
                navController = navController,
                viewModel = authViewModel
            ) {
                navController.navigate("product_list") {
                    popUpTo("login") { inclusive = true }
                }
            }
        }

        composable("sign_up") {
            SignUpScreen(
                viewModel = authViewModel,
                onSignUpSuccess = {
                    navController.navigate("login") {
                        popUpTo("sign_up") { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.navigate("login") {
                        popUpTo("sign_up") { inclusive = true }
                    }
                }
            )
        }

        composable("product_list") {
            MainScreen(
                viewModel = viewModel,
                navController = navController,
                onProductClick = { product ->
                    navController.navigate("product_detail/${product.id}")
                }
            )
        }

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