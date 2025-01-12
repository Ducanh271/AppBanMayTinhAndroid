package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
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
import com.example.myapplication.viewmodel.OrderViewModel
import com.example.myapplication.viewmodel.OrderViewModelFactory
import com.example.myapplication.data.repository.OrdersRepository
import com.example.myapplication.utils.SharedPrefUtils
import vn.zalopay.sdk.Environment
import vn.zalopay.sdk.ZaloPaySDK

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Cấu hình StrictMode (chỉ nên dùng khi phát triển, không khuyến khích trong sản xuất)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        // Khởi tạo ZaloPay SDK
        ZaloPaySDK.init(2553, Environment.SANDBOX)

        // Lấy userId từ SharedPreferences
        val userId = SharedPrefUtils.getUserId(this) ?: run {
            // Điều hướng đến màn hình đăng nhập nếu userId không tồn tại
            finish()
            return
        }

        // Tạo các repository cần thiết
        val ordersRepository = OrdersRepository(ApiService.orderApi)
        val productRepository = ProductRepository(ApiService.productApi)
        val authRepository = AuthRepository(ApiService.userApi)
        val cartRepository = CartRepository(ApiService.cartApi)

        setContent {
            // Khởi tạo các ViewModel cần thiết
            val productViewModel: ProductViewModel = viewModel(
                factory = ProductViewModelFactory(productRepository)
            )

            val authViewModel: AuthViewModel = viewModel(
                factory = AuthViewModelFactory(authRepository)
            )
            val cartViewModel: CartViewModel = viewModel(
                factory = CartViewModelFactory(cartRepository)
            )
            val orderViewModel: OrderViewModel = viewModel(
                factory = OrderViewModelFactory(ordersRepository, userId)
            )

            // Kiểm tra trạng thái đăng nhập
            LaunchedEffect(Unit) {
                authViewModel.checkLoginStatus(this@MainActivity)
            }

            // Điều hướng màn hình
            val navController = rememberNavController()
            AppNavigation(
                navController = navController,
                viewModel = productViewModel,
                authViewModel = authViewModel,
                cartViewModel = cartViewModel,
                orderViewModel = orderViewModel
            )
        }
    }

    // Xử lý callback từ ZaloPay
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        ZaloPaySDK.getInstance().onResult(intent)
    }
}
