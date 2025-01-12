package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.models.OrderResponse
import com.example.myapplication.utils.SharedPrefUtils
import com.example.myapplication.viewmodel.OrderViewModel
import java.text.NumberFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    viewModel: OrderViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val userId = SharedPrefUtils.getUserId(context) ?: "" // Cung cấp giá trị mặc định nếu null

//    // Gọi hàm fetchOrders khi màn hình được load
//    LaunchedEffect(userId) {
//        if (userId.isNotEmpty()) {
//            viewModel.fetchOrders(userId)
//        } else {
//            // Xử lý trường hợp không có userId, ví dụ yêu cầu người dùng đăng nhập lại
//        }
//    }
    // Gọi hàm fetchAllOrders khi màn hình được load
    LaunchedEffect(Unit) {
        viewModel.fetchAllOrders() // Không cần truyền userId
    }

    // Lấy trạng thái từ ViewModel
    val orderState = viewModel.orders.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Orders") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Hiển thị tiến trình khi đang tải
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                // Kiểm tra nếu danh sách rỗng
                if (orderState.isEmpty()) {
                    Text(
                        text = "You have no orders.",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                } else {
                    // Hiển thị danh sách đơn hàng
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        items(orderState) { order ->
                            OrderCard(order = order, viewModel = viewModel) // Truyền viewModel vào đây
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun OrderCard(order: OrderResponse, viewModel: OrderViewModel) {
    val productNames = remember { mutableStateListOf<String?>() }

    // Lấy tên sản phẩm từ API dựa trên productId
    LaunchedEffect(order.items) {
        order.items.forEach { item ->
            val productName = viewModel.fetchProductName(item.productId) // Sử dụng hàm mới
            productNames.add(productName)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Order ID: ${order.id ?: "Unknown"}",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Products:",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Column {
                order.items.forEachIndexed { index, item ->
                    val productName = productNames.getOrNull(index) ?: "Loading..."
                    Text(
                        text = "- $productName (x${item.quantity})",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Total Price: ${NumberFormat.getCurrencyInstance().format(order.totalPrice)}",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Status: ${order.status ?: "Unknown"}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray
                )
            )
        }
    }
}


