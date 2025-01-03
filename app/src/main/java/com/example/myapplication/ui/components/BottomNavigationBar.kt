package com.example.myapplication.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    navController: NavController
) {
    // Lấy route hiện tại từ NavController
    val currentRoute = navController.currentBackStackEntryAsState()?.value?.destination?.route

    NavigationBar {
        // Tab "Cho bạn"
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Cho bạn") },
            selected = currentRoute == "product_list",
            onClick = {
                onTabSelected(0) // Tab vị trí 0
                if (currentRoute != "product_list") {
                    navController.navigate("product_list") {
                        launchSingleTop = true // Ngăn điều hướng trùng lặp
                        restoreState = true // Khôi phục trạng thái trước đó
                    }
                }
            }
        )

        // Tab "Dạo"
        NavigationBarItem(
            icon = { Icon(Icons.Default.Explore, contentDescription = "Explore") },
            label = { Text("Dạo") },
            selected = currentRoute == "explore",
            onClick = {
                onTabSelected(1) // Tab vị trí 1
                if (currentRoute != "explore") {
                    navController.navigate("explore") {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        )

        // Tab "Tin nhắn"
        NavigationBarItem(
            icon = { Icon(Icons.Default.Message, contentDescription = "Messages") },
            label = { Text("Tin nhắn") },
            selected = currentRoute == "messages",
            onClick = {
                onTabSelected(2) // Tab vị trí 2
                if (currentRoute != "messages") {
                    navController.navigate("messages") {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        )

        // Tab "Giỏ hàng"
        NavigationBarItem(
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Cart") },
            label = { Text("Giỏ hàng") },
            selected = currentRoute == "cart",
            onClick = {
                onTabSelected(3) // Tab vị trí 3
                if (currentRoute != "cart") {
                    navController.navigate("cart") {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        )

        // Tab "Tài khoản"
        NavigationBarItem(
            icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Account") },
            label = { Text("Tài khoản") },
            selected = currentRoute == "account",
            onClick = {
                onTabSelected(4) // Tab vị trí 4
                if (currentRoute != "account") {
                    navController.navigate("account") {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        )
    }
}