package com.example.myapplication.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun BottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    navController: NavController // Thêm NavController vào
) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Cho bạn") },
            selected = selectedTab == 0,
            onClick = {
                onTabSelected(0)

            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Explore, contentDescription = "Dạo") },
            label = { Text("Dạo") },
            selected = selectedTab == 1,
            onClick = {
                onTabSelected(1)

            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Message, contentDescription = "Messages") },
            label = { Text("Tin nhắn") },
            selected = selectedTab == 2,
            onClick = {
                onTabSelected(2)

            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Cart") },
            label = { Text("Giỏ hàng") },
            selected = selectedTab == 3,
            onClick = {
                onTabSelected(3)
                navController.navigate("cart") // Điều hướng tới màn hình Giỏ hàng
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Account") },
            label = { Text("Tài khoản") },
            selected = selectedTab == 4,
            onClick = {
                onTabSelected(4)

            }
        )
    }
}
