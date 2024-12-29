package com.example.myapplication.ui.screens

import android.content.Context
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.models.OrderItem
import com.example.myapplication.utils.SharedPrefUtils
import com.example.myapplication.viewmodel.CartViewModel
import com.example.myapplication.viewmodel.OrderViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreenCart(
    viewModel: CartViewModel,
    orderViewModel: OrderViewModel, // Thêm OrderViewModel
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val cartState = viewModel.cartState.collectAsState().value
    val totalPrice = viewModel.calculateTotalPrice()
    val userId = SharedPrefUtils.getUserId(context) // Lấy userId từ SharedPreferences
    val scope = rememberCoroutineScope() // Sử dụng coroutine trong Composable

    var recipientName by remember { mutableStateOf("") }
    var recipientPhone by remember { mutableStateOf("") }
    var recipientAddress by remember { mutableStateOf("") }

    fun toastMsg(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thanh toán giỏ hàng") },
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
            // Tổng giá tiền
            val formattedTotalPrice = NumberFormat.getCurrencyInstance().format(totalPrice)
            Text(
                text = "Tổng tiền: $formattedTotalPrice",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Danh sách sản phẩm trong giỏ hàng
            Text(
                text = "Danh sách sản phẩm:",
                style = MaterialTheme.typography.titleMedium
            )
            cartState?.items?.forEach { cartItem ->
                val formattedPrice = NumberFormat.getCurrencyInstance().format(cartItem.price)
                Text(
                    text = "- ${cartItem.title} (Số lượng: ${cartItem.quantity}, Giá: $formattedPrice)",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Form nhập thông tin
            Text("Nhập thông tin thanh toán", style = MaterialTheme.typography.titleMedium)

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

            // Nút "Thanh toán khi nhận hàng"
            Button(
                onClick = {
                    if (recipientName.isNotEmpty() && recipientPhone.isNotEmpty() && recipientAddress.isNotEmpty()) {
                        if (userId != null) {
                            scope.launch {
                                try {
                                    // Chuyển đổi từ CartItem sang OrderItem
                                    val orderItems = cartState?.items?.map { cartItem ->
                                        OrderItem(
                                            productId = cartItem.productId, // Hoặc thuộc tính tương ứng của OrderItem
                                            quantity = cartItem.quantity
                                        )
                                    } ?: emptyList()

                                    // Gửi thông tin đơn hàng lên server
                                    orderViewModel.handleCartCashOnDeliveryPayment(
                                        userId = userId,
                                        orderItems = orderItems, // Đây là List<OrderItem>
                                        recipientPhone = recipientPhone,
                                        recipientAddress = recipientAddress,
                                        context = context,
                                    )
                                } catch (e: Exception) {
                                    toastMsg(context, "Đặt hàng thất bại: ${e.message}")
                                }
                            }
                        } else {
                            toastMsg(context, "Vui lòng đăng nhập trước!")
                        }
                    } else {
                        toastMsg(context, "Vui lòng nhập đầy đủ thông tin!")
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
