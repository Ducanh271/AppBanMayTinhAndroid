package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.models.Product
import com.example.myapplication.ui.components.ProductItem
import com.example.myapplication.viewmodel.ProductViewModel

@Composable
fun ProductListScreen(
    viewModel: ProductViewModel,
    onProductClick: (Product) -> Unit // Callback khi nhấn vào sản phẩm
) {
    val productList by viewModel.productList.collectAsState() // Danh sách sản phẩm
    val isLoading by viewModel.isLoading.collectAsState()     // Trạng thái loading
    val errorMessage by viewModel.errorMessage.collectAsState() // Thông báo lỗi

    // Gọi fetchProducts khi màn hình được khởi chạy
    LaunchedEffect(Unit) {
        viewModel.fetchProducts()
    }

    // Hiển thị UI dựa trên trạng thái hiện tại
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        when {
            isLoading -> {
                // Trạng thái đang tải dữ liệu
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
                // Hiển thị danh sách sản phẩm
                LazyColumn(
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(productList) { product ->
                        ProductItem(
                            product = product,
                            onClick = {
                                if (!product.id.isNullOrEmpty()) { // Kiểm tra product.id trước khi gọi
                                    println("Product ID: ${product.id}")
                                    onProductClick(product) // Chỉ truyền product khi ID hợp lệ
                                } else {
                                    println("Error: Product ID is null for product: ${product.title}")
                                }
                            },
                            onAddToCart = {
                                // Placeholder: Chưa xử lý hành động Add to Cart
                                println("Added to cart: ${product.title}")
                            }
                        )
                    }
                }
            }
        }
    }
}
