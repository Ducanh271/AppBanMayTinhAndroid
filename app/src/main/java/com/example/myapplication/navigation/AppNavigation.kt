//package com.example.myapplication.navigation
//
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import com.example.myapplication.ui.screens.LoginScreen
//import com.example.myapplication.ui.screens.MainScreen
//import com.example.myapplication.ui.screens.ProductDetailScreen
//import com.example.myapplication.ui.screens.SignUpScreen
//import com.example.myapplication.viewmodel.AuthViewModel
//import com.example.myapplication.viewmodel.ProductViewModel
//
//@Composable
//fun AppNavigation(
//    navController: NavHostController,
//    viewModel: ProductViewModel,
//    authViewModel: AuthViewModel
//) {
//    // Lấy context hiện tại
//    val context = LocalContext.current
//
//    // Theo dõi trạng thái đăng nhập
//    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
//
//    // Kiểm tra trạng thái đăng nhập khi khởi động
//    LaunchedEffect(Unit) {
//        authViewModel.checkLoginStatus(context)
//    }
//
//    // Xử lý điều hướng khi trạng thái đăng nhập thay đổi
//    LaunchedEffect(isLoggedIn) {
//        if (isLoggedIn) {
//            navController.navigate("product_list") {
//                popUpTo("login") { inclusive = true }
//            }
//        } else {
//            // Khi đăng xuất, điều hướng về màn hình login
//            navController.navigate("login") {
//                popUpTo(0) { inclusive = true }
//            }
//        }
//    }
//
//    // Cấu hình điều hướng
//    NavHost(
//        navController = navController,
//        startDestination = if (isLoggedIn) "product_list" else "login"
//    ) {
//        // Màn hình đăng nhập
//        composable("login") {
//            LoginScreen(
//                navController = navController,
//                viewModel = authViewModel
//            ) {
//                navController.navigate("product_list") {
//                    popUpTo("login") { inclusive = true }
//                }
//            }
//        }
//
//        // Màn hình đăng ký
//        composable("sign_up") {
//            SignUpScreen(
//                viewModel = authViewModel,
//                onSignUpSuccess = {
//                    navController.navigate("login") {
//                        popUpTo("sign_up") { inclusive = true }
//                    }
//                },
//                onLoginClick = {
//                    navController.navigate("login") {
//                        popUpTo("sign_up") { inclusive = true }
//                    }
//                }
//            )
//        }
//
//        // Màn hình danh sách sản phẩm
//        composable("product_list") {
//            MainScreen(
//                viewModel = viewModel,
//                navController = navController,
//                authViewModel = authViewModel,  // Truyền authViewModel vào MainScreen
//                onProductClick = { product ->
//                    navController.navigate("product_detail/${product.id}")
//                }
//            )
//        }
//
//        // Màn hình chi tiết sản phẩm
//        composable("product_detail/{productId}") { backStackEntry ->
//            // Lấy productId từ arguments
//            val productId = backStackEntry.arguments?.getString("productId") ?: return@composable
//
//            // Fetch thông tin sản phẩm khi vào màn hình
//            LaunchedEffect(productId) {
//                viewModel.fetchProductById(productId)
//            }
//
//            // Hiển thị màn hình chi tiết sản phẩm
//            val product by viewModel.selectedProduct.collectAsState()
//            Box(modifier = Modifier.fillMaxSize()) {
//                if (product != null) {
//                    ProductDetailScreen(
//                        product = product!!,
//                        onBack = { navController.popBackStack() },
//                        //onBuyNow Mua ngay
//                        onBuyNow = { selectedProduct ->
//                            // Điều hướng sang màn hình thanh toán khi nhấn "Mua ngay"
//                            navController.navigate("checkout/${selectedProduct.id}")
//                        }
//                    )
//                } else {
//                    // Hiển thị loading khi đang fetch dữ liệu
//                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
//                }
//            }
//        }
//    }
//}

//newtest

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
import com.example.myapplication.ui.screens.*
import com.example.myapplication.viewmodel.AuthViewModel
import com.example.myapplication.viewmodel.ProductViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    viewModel: ProductViewModel,
    authViewModel: AuthViewModel
) {
    // Lấy context hiện tại
    val context = LocalContext.current

    // Theo dõi trạng thái đăng nhập
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    // Kiểm tra trạng thái đăng nhập khi khởi động
    LaunchedEffect(Unit) {
        authViewModel.checkLoginStatus(context)
    }

    // Xử lý điều hướng khi trạng thái đăng nhập thay đổi
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            navController.navigate("product_list") {
                popUpTo("login") { inclusive = true }
            }
        } else {
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    // Cấu hình điều hướng
    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) "product_list" else "login"
    ) {
        // Màn hình đăng nhập
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

        // Màn hình đăng ký
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

        // Màn hình danh sách sản phẩm
        composable("product_list") {
            MainScreen(
                viewModel = viewModel,
                navController = navController,
                authViewModel = authViewModel,
                onProductClick = { product ->
                    navController.navigate("product_detail/${product.id}")
                }
            )
        }

        // Màn hình chi tiết sản phẩm
        composable("product_detail/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: return@composable

            // Fetch thông tin sản phẩm khi vào màn hình
            LaunchedEffect(productId) {
                viewModel.fetchProductById(productId)
            }

            val product by viewModel.selectedProduct.collectAsState()
            Box(modifier = Modifier.fillMaxSize()) {
                if (product != null) {
                    ProductDetailScreen(
                        product = product!!,
                        onBack = { navController.popBackStack() },
                        onBuyNow = { selectedProduct ->
                            navController.navigate("checkout/${selectedProduct.id}")
                        }
                    )
                } else {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        }

//        // Màn hình thanh toán
//        composable("checkout/{productId}") { backStackEntry ->
//            val productId = backStackEntry.arguments?.getString("productId") ?: return@composable
//
//            // Hiển thị màn hình thanh toán
//            CheckoutScreen(
//                productId = productId,
//                onBack = { navController.popBackStack() },
////                onPurchaseComplete = {
////                    // Điều hướng trở lại danh sách sản phẩm sau khi mua
////                    navController.navigate("product_list") {
////                        popUpTo("checkout/{productId}") { inclusive = true }
////                    }
//
//            )
//        }
        // Màn hình thanh toán
        composable("checkout/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: return@composable
            LaunchedEffect(productId) {
                viewModel.fetchProductById(productId)
            }

            val product by viewModel.selectedProduct.collectAsState()
            Box(modifier = Modifier.fillMaxSize()) {
                if (product != null) {
                    CheckoutScreen(
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

