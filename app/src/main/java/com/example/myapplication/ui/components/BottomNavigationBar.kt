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
    val currentRoute = navController.currentBackStackEntryAsState()?.value?.destination?.route

    NavigationBar {
        // Tab "Trang chủ"
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Trang chủ") },
            selected = currentRoute == "product_list",
            onClick = {
                onTabSelected(0)
                if (currentRoute != "product_list") {
                    navController.navigate("product_list") {
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
                onTabSelected(1)
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
                onTabSelected(2)
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