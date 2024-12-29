package com.example.myapplication.ui.screens

import OrderApi
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.data.api.ApiService
import com.example.myapplication.data.models.OrderItem
import com.example.myapplication.data.models.OrderRequest
import com.example.myapplication.data.models.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.repository.AuthRepository
import com.example.myapplication.utils.SharedPrefUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(product: Product, onBack: () -> Unit) {
    var recipientName by remember { mutableStateOf("") }
    var recipientPhone by remember { mutableStateOf("") }
    var recipientAddress by remember { mutableStateOf("") }
    val context = LocalContext.current
    val userId = SharedPrefUtils.getUserId(context)
    val scope = rememberCoroutineScope() // Để sử dụng coroutine trong Composable
    val authRepository = AuthRepository(ApiService.userApi) // Khởi tạo AuthRepository



    // Hàm xử lý logic thanh toán khi nhận hàng
    suspend fun handleCashOnDeliveryPayment(
        userId: String,
        product: Product,
        recipientAddress: String,
        recipientPhone: String,
        context: Context
    ) {
        val orderRequest = OrderRequest(
            userId = userId,
            items = listOf(OrderItem(productId = product.id, quantity = 1)),
            address = recipientAddress,
            phoneNumber = recipientPhone
        )

        try {
            // Sử dụng withContext để chuyển sang dispatcher IO cho việc gọi API
            val response = withContext(Dispatchers.IO) {
                ApiService.orderApi.createOrder(orderRequest)
            }

            if (response.isSuccessful) {
                Toast.makeText(context, "Đặt hàng thành công! Mã đơn: ${response.body()?.orderId}", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Đặt hàng thất bại: ${response.message()}", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Lỗi mạng: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thanh toán") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Ảnh sản phẩm
            Image(
                painter = rememberAsyncImagePainter(product.image),
                contentDescription = product.title,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Thông tin sản phẩm
            Text(text = product.title, style = MaterialTheme.typography.headlineMedium)
            Text("Giá sản phẩm ", style = MaterialTheme.typography.labelLarge)
            Text(text = "${product.price} $", style = MaterialTheme.typography.titleLarge)

            // 3. Nhập thông tin thanh toán
            Text("Nhập thông tin địa chỉ thanh toán", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = recipientName,
                onValueChange = { recipientName = it },
                label = { Text("Họ và Tên người nhận") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = recipientPhone,
                onValueChange = { recipientPhone = it },
                label = { Text("Số điện thoại người nhận") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = recipientAddress,
                onValueChange = { recipientAddress = it },
                label = { Text("Địa chỉ nhận hàng") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Nút Thanh toán khi nhận hàng
            Button(
                onClick = {
                    if (recipientName.isNotEmpty() && recipientPhone.isNotEmpty() && recipientAddress.isNotEmpty()) {
                        if (userId != null) {  // Kiểm tra nếu userId đã có
                            // Gọi coroutine để xử lý thanh toán khi nhận hàng
                            scope.launch {
                                handleCashOnDeliveryPayment(
                                    userId = userId, // Thay bằng ID người dùng thực tế
                                    product = product,
                                    recipientAddress = recipientAddress,
                                    recipientPhone = recipientPhone,
                                    context = context
                                )
                            }
                        } else {
                            Toast.makeText(context, "Vui lòng đăng nhập trước!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(45.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                shape = RoundedCornerShape(6.dp),
            ) {
                Text(text = "Thanh toán khi nhận hàng", fontSize = 17.sp, color = Color.White)
            }
        }
    }
}
