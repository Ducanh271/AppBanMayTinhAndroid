package com.example.myapplication.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.models.CartItem
import com.example.myapplication.viewmodel.CartViewModel
import java.text.NumberFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreenCart(
    viewModel: CartViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val cartState = viewModel.cartState.collectAsState().value
    val totalPrice = viewModel.calculateTotalPrice()

    var recipientName by remember { mutableStateOf("") }
    var recipientPhone by remember { mutableStateOf("") }
    var recipientAddress by remember { mutableStateOf("") }
    var messageToShop by remember { mutableStateOf("") }

    fun toastMsg(
        context: Context,
        msg: String
    ) {
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
                .verticalScroll(rememberScrollState()), // Thêm khả năng cuộn
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
                // Hiển thị tên sản phẩm, số lượng và giá
                val formattedPrice = NumberFormat.getCurrencyInstance().format(cartItem.price)
                Text(
                    text = "- ${cartItem.title} (Số lượng: ${cartItem.quantity}, Giá: $formattedPrice)",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

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

            // Nút thanh toán
            Button(
                onClick = {
                    toastMsg(
                        context = context,
                        msg = "Đã xác nhận thanh toán!"
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
            ) {
                Text("Thanh toán")
            }
        }
    }
}
