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
    cartViewModel: CartViewModel, // Thêm tham số này
    navController: NavController,
    authViewModel: AuthViewModel,

    ) {
    val context = LocalContext.current // Đưa LocalContext ra ngoài phạm vi @Composable

    // Trạng thái cho ScrollableTabRow
    var selectedTabTopRow by remember { mutableStateOf(0) }
    // Trạng thái cho BottomNavigationBar
    var selectedTabBottomBar by remember { mutableStateOf(0) }

    var selectedTab by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    val categories by viewModel.categories.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchCategories()
    }
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { /* TODO: Thêm menu logic */ }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { query ->
                                    searchQuery = query
                                    viewModel.searchProducts(query)
                                },
                                placeholder = { Text("Nhập sản phẩm ...") },
                                singleLine = true,
                                leadingIcon = {
                                    Icon(Icons.Default.Search, contentDescription = "Search Icon")
                                },
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = {  navController.navigate("cart") } ) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                            }
                            IconButton(
                                onClick = {
                                    // Sử dụng authViewModel để xử lý đăng xuất thay vì trực tiếp
                                    authViewModel.logout(context)
                                    // Navigation sẽ được xử lý tự động bởi LaunchedEffect trong AppNavigation
                                }
                            ) {
                                Icon(Icons.Default.Logout, contentDescription = "Logout")
                            }
                        }
                    }
                )

                if (categories.isNotEmpty()) {
                    val allCategories = listOf("All") + categories
                    ScrollableTabRow(selectedTabIndex = selectedTabTopRow, edgePadding = 8.dp) {
                        allCategories.forEachIndexed { index, category ->
                            Tab(
                                selected = selectedTabTopRow == index,
                                onClick = {
                                    viewModel.selectedCategory.value = category // Cập nhật selectedCategory
                                    selectedTabTopRow = index
                                    if (category == "All") {

                                        viewModel.fetchProducts()
                                    } else {
                                        viewModel.fetchProductsByCategory(category)
                                    }
                                },
                                text = { Text(category) }
                            )
                        }
                    }
                } else {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
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
            ProductListScreen(viewModel = viewModel,
                cartViewModel = cartViewModel,
                onProductClick = onProductClick)
        }
    }
}