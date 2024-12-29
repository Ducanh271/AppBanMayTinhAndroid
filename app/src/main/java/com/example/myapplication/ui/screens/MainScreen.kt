package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.ui.components.BottomNavigationBar
import com.example.myapplication.viewmodel.ProductViewModel
import com.example.myapplication.utils.SharedPrefUtils
import com.example.myapplication.data.models.Product
import com.example.myapplication.viewmodel.AuthViewModel
import com.example.myapplication.viewmodel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: ProductViewModel,
    onProductClick: (Product) -> Unit,
    cartViewModel: CartViewModel,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { /* TODO: Menu logic */ }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                            OutlinedTextField(
                                value = "",
                                onValueChange = { query ->
                                    viewModel.searchProducts(query)
                                },
                                placeholder = { Text("Nhập sản phẩm...") },
                                singleLine = true,
                                leadingIcon = {
                                    Icon(Icons.Default.Search, contentDescription = "Search Icon")
                                },
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { navController.navigate("cart") }) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                            }
                            IconButton(onClick = {
                                authViewModel.logout(context)
                            }) {
                                Icon(Icons.Default.Logout, contentDescription = "Logout")
                            }
                        }
                    }
                )
            }
        },
        bottomBar = {
            BottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                navController = navController // Truyền NavController vào
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> {
                    ProductListScreen(
                        viewModel = viewModel,
                        cartViewModel = cartViewModel,
                        onProductClick = onProductClick
                    )
                }
                3 -> {
                    CheckoutScreenCart(
                        viewModel = cartViewModel,
                        onBack = { navController.navigateUp() }
                    )
                }
                // Xử lý các tab khác ở đây
            }
        }
    }
}
