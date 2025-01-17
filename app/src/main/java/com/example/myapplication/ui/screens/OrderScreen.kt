package com.example.myapplication.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
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
import com.example.myapplication.ui.components.OrderItemRow
import com.example.myapplication.utils.SharedPrefUtils
import com.example.myapplication.viewmodel.OrderViewModel
import java.text.NumberFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    viewModel: OrderViewModel,
    onOrderClick: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val userId = SharedPrefUtils.getUserId(context) ?: "" // Cung cấp giá trị mặc định nếu null

    // Gọi hàm fetchOrders khi màn hình được load
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            viewModel.fetchOrders(userId)
        } else {
            // Xử lý trường hợp không có userId, ví dụ yêu cầu người dùng đăng nhập lại
        }
    }
    // Gọi hàm fetchOrders khi màn hình được load
    LaunchedEffect(Unit) {
        viewModel.fetchOrders(userId)
    }

    // Lấy trạng thái từ ViewModel
    val orderList = viewModel.orderList.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Đơn hàng của bạn") },
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
                if (orderList.isEmpty()) {
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
                        items(orderList) { order ->
                            OrderItemRow(
                                orderItemRow = order,
                                onClick = { orderId ->
                                    Log.d("OrderScreen", "Navigating with orderId: $orderId") // Log kiểm tra
                                    onOrderClick(orderId) }


                            )
                        }
                    }
                }
            }
        }
    }
}
