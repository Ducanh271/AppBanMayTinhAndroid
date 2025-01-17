package com.example.myapplication.navigation

import android.util.Log
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
import com.example.myapplication.data.models.AddToCartRequest
import com.example.myapplication.ui.screens.*
import com.example.myapplication.utils.SharedPrefUtils
import com.example.myapplication.utils.LocalUserId
import com.example.myapplication.viewmodel.AuthViewModel
import com.example.myapplication.viewmodel.CartViewModel
import com.example.myapplication.viewmodel.ProductViewModel
import com.example.myapplication.viewmodel.OrderViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    viewModel: ProductViewModel,
    authViewModel: AuthViewModel,
    orderViewModel: OrderViewModel,
    cartViewModel: CartViewModel
) {
    // Lấy context hiện tại
    val context = LocalContext.current

    // Lấy UserID từ SharedPreferences
    val userId = remember { SharedPrefUtils.getUserId(context) }

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

    // Cung cấp UserID thông qua CompositionLocalProvider
    CompositionLocalProvider(LocalUserId provides userId) {
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


            // Thêm màn hình home để auto thanh toan xong truyền về

            composable("home") {
                MainScreen(
                    viewModel = viewModel,
                    navController = navController,
                    authViewModel = authViewModel,
                    cartViewModel = cartViewModel,
                    onProductClick = { product ->
                        navController.navigate("product_detail/${product.id}")
                    }
                )
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
                    authViewModel = authViewModel,
                    cartViewModel = cartViewModel,
                    onProductClick = { product ->
                        navController.navigate("product_detail/${product.id}")
                    }
                )
            }

            composable("product_detail/{productId}") { backStackEntry ->
                val productId =
                    backStackEntry.arguments?.getString("productId") ?: return@composable

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
                            },
                            cartViewModel = cartViewModel
                        )
                    } else {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }
            }

            //điều hướng tới ordersScreen
            composable("orders") {
                OrderScreen(viewModel = orderViewModel,
                    onOrderClick = { orderId ->
                        navController.navigate("order_detail/$orderId")
                    },
                    onBack = { navController.popBackStack() })


            }

            //Điều hướng tới chatscreen

            composable("chat") {
                ChatScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            // điều hướng đến oderDetailScreen
            composable("order_detail/{orderId}") { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: return@composable
                Log.d(
                    "AppNavigation",
                    "Navigating to OrderDetailScreen with orderId: $orderId"
                ) // Log kiểm tra

                OrderDetailScreen(
                    orderId = orderId,
                    viewModel = orderViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("cart") {
                CartScreen(
                    viewModel = cartViewModel,
                    onBack = { navController.popBackStack() },
                    onCheckout = { navController.navigate("checkout_cart") }
                )
            }

            composable("checkout_cart") {
                CheckoutScreenCart(
                    viewModel = cartViewModel,
                    orderViewModel = orderViewModel,
                    onBack = { navController.popBackStack() },
                    onNavigateToHome = {
                        navController.navigate("home") {
                            popUpTo("product_list") { inclusive = true }
                        }
                    }
                )
            }


            //call back lại deitailscreen tự động
// Callback lại DetailScreen tự động
            composable("checkout/{productId}") { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: return@composable

                LaunchedEffect(productId) {
                    viewModel.fetchProductById(productId)
                }

                val product by viewModel.selectedProduct.collectAsState()
                Box(modifier = Modifier.fillMaxSize()) {
                    if (product != null) {
                        CheckoutScreen(
                            orderViewModel = orderViewModel,
                            product = product!!,
                            onBack = { navController.popBackStack() },
                            onNavigateToOrderDetail = { orderId ->
                                navController.navigate("order_detail/$orderId") {
                                    popUpTo("checkout/$productId") { inclusive = true }
                                }
                            }
                        )
                    } else {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }
            }





            composable("account") {
                AccountScreen(
                    navController = navController,
                    authViewModel = authViewModel
                )
            }
        }
    }
}