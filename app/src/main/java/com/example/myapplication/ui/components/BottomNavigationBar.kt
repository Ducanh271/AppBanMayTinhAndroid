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
    navController: NavController
) {
    NavigationBar {
        // Tab "Sản phẩm"
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Sản phẩm") },
            selected = selectedTab == 0,
            onClick = {
                onTabSelected(0)
                navController.navigate("product_list")
            }
        )

        // Tab "Khám phá"
        NavigationBarItem(
            icon = { Icon(Icons.Default.Explore, contentDescription = "Explore") },
            label = { Text("Khám phá") },
            selected = selectedTab == 1,
            onClick = {
                onTabSelected(1)
                navController.navigate("explore")
            }
        )

        // Tab "Tin nhắn"
        NavigationBarItem(
            icon = { Icon(Icons.Default.Message, contentDescription = "Message") },
            label = { Text("Tin nhắn") },
            selected = selectedTab == 2,
            onClick = {
                onTabSelected(2)
                navController.navigate("messages")
            }
        )

        // Tab "Giỏ hàng"
        NavigationBarItem(
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Cart") },
            label = { Text("Giỏ hàng") },
            selected = selectedTab == 3,
            onClick = {
                onTabSelected(3)
                navController.navigate("cart")
            }
        )

        // Tab "Tài khoản"
        NavigationBarItem(
            icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Account") },
            label = { Text("Tài khoản") },
            selected = selectedTab == 4,
            onClick = {
                onTabSelected(4)
                navController.navigate("account")
            }
        )
    }
}