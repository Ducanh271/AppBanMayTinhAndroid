package com.example.myapplication.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.ui.components.BottomNavigationBar
import com.example.myapplication.utils.SharedPrefUtils
import com.example.myapplication.viewmodel.AuthViewModel
import androidx.compose.runtime.LaunchedEffect

@Composable
fun AccountScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current

    // Lấy thông tin người dùng từ SharedPreferences
    val userName = remember { SharedPrefUtils.getUserName(context) ?: "Người dùng" }
    val userEmail = remember { SharedPrefUtils.getUserEmail(context) ?: "example@gmail.com" }

// Kiểm tra nếu thông tin chưa được lưu
    if (userName.isEmpty() || userEmail.isEmpty()) {
        LaunchedEffect(Unit) {
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedTab = 4, // Tab "Tài khoản" được chọn
                onTabSelected = { tab ->
                    when (tab) {
                        0 -> navController.navigate("product_list")
                        1 -> navController.navigate("explore")
                        2 -> navController.navigate("messages")
                        3 -> navController.navigate("cart")
                        4 -> {} // Đã ở trang tài khoản
                    }
                },
                navController = navController
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Phần thông tin người dùng
            UserInfoSection(userName, userEmail)

            // Các chức năng
            FunctionSection(navController, authViewModel)
        }
    }
}

@Composable
fun UserInfoSection(userName: String, userEmail: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Ảnh đại diện
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // Thêm kiểm tra tài nguyên hoặc sử dụng hình ảnh mặc định
            val avatarResId = try {
                R.drawable.ic_user_avatar // Tài nguyên hình ảnh tùy chỉnh
            } catch (e: Exception) {
                android.R.drawable.ic_menu_help // Hình ảnh mặc định
            }

            Image(
                painter = painterResource(id = avatarResId),
                contentDescription = "User Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(70.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Tên người dùng
        Text(
            text = userName,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary
        )

        // Email người dùng
        Text(
            text = userEmail,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun FunctionSection(navController: NavController, authViewModel: AuthViewModel) {
    val context = LocalContext.current // Lấy context tại đây

    Column(modifier = Modifier.fillMaxWidth()) {
        // Đơn hàng của tôi
        FunctionItem(
            title = "Đơn hàng của tôi",
            icon = Icons.Default.ShoppingCart,
            onClick = { navController.navigate("orders") }
        )

        // Cài đặt tài khoản
        FunctionItem(
            title = "Cài đặt tài khoản",
            icon = Icons.Default.Settings,
            onClick = { navController.navigate("edit_account") }
        )

        // Đăng xuất
        FunctionItem(
            title = "Đăng xuất",
            icon = Icons.Default.ExitToApp,
            onClick = {
                authViewModel.logout(context) // Gọi hàm logout trong AuthViewModel
                navController.navigate("login") {
                    popUpTo("product_list") { inclusive = true }
                }
            }
        )
    }
}

@Composable
fun FunctionItem(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon chức năng
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier
                .size(24.dp)
                .padding(end = 16.dp)
        )

        // Tên chức năng
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
