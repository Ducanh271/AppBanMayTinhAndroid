package com.example.myapplication.ui.screens

import android.util.Log
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.Locale
import com.example.myapplication.data.models.OrderResponse
import com.example.myapplication.ui.components.OrderDetailItem
import com.example.myapplication.viewmodel.OrderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    orderId: String,
    viewModel: OrderViewModel,
    onBack: () -> Unit
) {
    LaunchedEffect(orderId) {
        Log.d("OrderDetailScreen", "Loading order details for orderId: $orderId")
        viewModel.loadOrder(orderId)
    }

    val isLoading by viewModel.isLoading.collectAsState()
    val orderDetail by viewModel.orderItemList.collectAsState()

    // Hàm định dạng tiền VND
    fun formatCurrency(amount: Double): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        return formatter.format(amount)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết đơn hàng", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
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
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (orderDetail != null) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Thông tin đơn hàng
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "ID: ${orderDetail!!.id}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = "Địa chỉ: ${orderDetail!!.address}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = "Số điện thoại: ${orderDetail!!.phoneNumber}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = "Ngày tạo: ${orderDetail!!.createdAt}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = "Trạng thái: ${orderDetail!!.status}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    // Danh sách mặt hàng
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        items(orderDetail!!.items) { orderItem ->
                            OrderDetailItem(orderItem = orderItem)
                        }
                    }
                }

                // Tổng tiền cố định ở giữa dưới cùng
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6)),
                        modifier = Modifier.align(Alignment.Center) // Đảm bảo nội dung chính giữa
                    ) {
                        Text(
                            text = "Tổng tiền: ${formatCurrency(orderDetail!!.total)}",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier
                                .fillMaxWidth() // Đảm bảo nội dung Text căn giữa theo chiều ngang
                                .padding(16.dp),
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center // Căn chữ chính giữa
                        )
                    }
                }

            } else {
                Text(
                    text = "Không tìm thấy chi tiết đơn hàng.",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}
