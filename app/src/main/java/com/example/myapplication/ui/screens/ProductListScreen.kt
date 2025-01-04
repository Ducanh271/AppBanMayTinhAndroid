// File: ProductListScreen.kt

package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.models.AddToCartRequest
import com.example.myapplication.data.models.Product
import com.example.myapplication.ui.components.ProductItem
import com.example.myapplication.utils.LocalUserId
import com.example.myapplication.utils.SharedPrefUtils
import com.example.myapplication.viewmodel.CartViewModel
import com.example.myapplication.viewmodel.ProductViewModel
import kotlinx.coroutines.flow.filter

@Composable
fun ProductListScreen(
    viewModel: ProductViewModel,
    cartViewModel: CartViewModel,
    onProductClick: (Product) -> Unit // Callback khi nhấn vào sản phẩm
) {
    val productList by viewModel.productList.collectAsState() // Danh sách sản phẩm
    val isLoading by viewModel.isLoading.collectAsState()     // Trạng thái loading
    val errorMessage by viewModel.errorMessage.collectAsState() // Thông báo lỗi
    val context = LocalContext.current
    val userId = SharedPrefUtils.getUserId(context) // Lấy userId từ SharedPreferences

    val lazyGridState = rememberLazyGridState() // Trạng thái cuộn của LazyVerticalGrid

    // Gọi fetchProducts khi màn hình được khởi chạy
    LaunchedEffect(Unit) {
        viewModel.fetchProducts()
    }

    // Theo dõi sự kiện cuộn và gọi loadNextPage
    LaunchedEffect(lazyGridState) {
        snapshotFlow { lazyGridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .filter { lastVisibleIndex ->
                lastVisibleIndex == lazyGridState.layoutInfo.totalItemsCount - 1
            }
            .collect {
                if (viewModel.selectedCategory.value == "All") {
                    viewModel.loadNextPage() // Gọi loadNextPage khi cuộn đến cuối
                }
            }
    }

    // Hiển thị UI dựa trên trạng thái hiện tại
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // Thêm màu nền trắng cho giống Shopee
            .padding(8.dp)
    ) {
        when {
            isLoading && productList.isEmpty() -> {
                // Trạng thái đang tải dữ liệu lần đầu
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            errorMessage != null -> {
                // Hiển thị thông báo lỗi
                Text(
                    text = errorMessage ?: "Unknown Error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            productList.isEmpty() -> {
                // Hiển thị thông báo khi danh sách rỗng
                Text(
                    text = "No products available",
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> {
                // Sửa LazyColumn thành LazyVerticalGrid để hiển thị dạng lưới
                LazyVerticalGrid(
                    state = lazyGridState, // Gắn trạng thái cuộn
                    columns = GridCells.Adaptive(minSize = 128.dp), // Tự động điều chỉnh số cột
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(productList) { product ->
                        ProductItem(
                            product = product,
                            onClick = {
                                if (!product.id.isNullOrEmpty()) {
                                    println("Product ID: ${product.id}")
                                    onProductClick(product)
                                } else {
                                    println("Error: Product ID is null for product: ${product.title}")
                                }
                            },
                            onAddToCart = { product, quantity ->
                                if (userId != null) {
                                    val request = AddToCartRequest(userId, product.id, quantity)
                                    cartViewModel.addToCart(request) { message ->
                                        println("Response: $message")
                                    }
                                } else {
                                    println("UserID is null. Please login.")
                                }
                            }
                        )
                    }

                    // Hiển thị ProgressBar ở cuối danh sách khi đang tải thêm
                    if (isLoading) {
                        item {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .wrapContentWidth(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }
            }
        }
    }
}
