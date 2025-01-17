package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.ui.components.BottomNavigationBar
import com.example.myapplication.viewmodel.ProductViewModel
import com.example.myapplication.utils.SharedPrefUtils
import com.example.myapplication.data.models.Product
import com.example.myapplication.viewmodel.AuthViewModel
import com.example.myapplication.viewmodel.CartViewModel
import kotlinx.coroutines.launch

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

    // Trạng thái cho Drawer và CoroutineScope
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Trạng thái cho các UI khác
    var selectedTabTopRow by remember { mutableStateOf(0) }
    var selectedTab by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    val categories by viewModel.categories.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchCategories()
    }

    LaunchedEffect(Unit) {
        viewModel.fetchCategories()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // Drawer nội dung hiển thị danh mục sản phẩm
            Box(
                Modifier
                    .fillMaxWidth(0.5f) // Drawer chiếm một nửa màn hình theo chiều ngang
                    .fillMaxHeight()
                    .background(Color(0xFFFF6699)) // Màu hồng
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize()
                ) {
                    // Hiển thị danh sách danh mục sản phẩm
                    val allCategories = listOf("Tất cả sản phẩm") + categories
                    allCategories.forEach { category ->
                        TextButton(
                            onClick = {
                                // Lọc sản phẩm theo danh mục
                                if (category == "Tất cả sản phẩm") {
                                    viewModel.fetchProducts() // Lấy tất cả sản phẩm
                                } else {
                                    viewModel.fetchProductsByCategory(category) // Lọc theo danh mục
                                }
                                // Đóng Drawer sau khi chọn
                                scope.launch { drawerState.close() }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = category,
                                color = Color.Black,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Start, // Căn lề trái cho văn bản
                                modifier = Modifier.fillMaxWidth() // Lấp đầy chiều ngang để hỗ trợ căn lề
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                Column {
                    TopAppBar(
                        title = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = {
                                    // Mở Drawer khi nhấn nút Menu
                                    scope.launch {
                                        if (drawerState.isClosed) {
                                            drawerState.open()
                                        } else {
                                            drawerState.close()
                                        }
                                    }
                                }) {
                                    Icon(Icons.Default.Menu, contentDescription = "Menu")
                                }
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = { query ->
                                        searchQuery = query
                                        viewModel.searchProducts(query)
                                    },
                                    placeholder = { Text("Nhập sản phẩm ...",
                                        style = MaterialTheme.typography.bodyMedium) },
                                    singleLine = true,
                                    textStyle = MaterialTheme.typography.bodyMedium,
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Search,
                                            contentDescription = "Search Icon"
                                        )
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(50.dp)
                                        .background(
                                            MaterialTheme.colorScheme.surface,
                                            shape = MaterialTheme.shapes.medium
                                        )
                                )
                                IconButton(onClick = { navController.navigate("cart") }) {
                                    Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                                }
                                IconButton(
                                    onClick = {
                                        // Xử lý đăng xuất
                                        authViewModel.logout(context)
                                    }
                                ) {
                                    Icon(Icons.Default.Logout, contentDescription = "Logout")
                                }
                            }
                        },
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    if (categories.isNotEmpty()) {
                        val allCategories = listOf("Tất cả sản phẩm") + categories
                        ScrollableTabRow(selectedTabIndex = selectedTabTopRow, edgePadding = 8.dp) {
                            allCategories.forEachIndexed { index, category ->
                                Tab(
                                    selected = selectedTabTopRow == index,
                                    onClick = {
                                        viewModel.selectedCategory.value = category
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
                    onTabSelected = { newTab ->
                        selectedTab = newTab
                    },
                    navController = navController
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                ProductListScreen(
                    viewModel = viewModel,
                    cartViewModel = cartViewModel,
                    onProductClick = onProductClick
                )
            }
        }
    }
}