package com.example.myapplication.ui.screens

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
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.data.models.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(product: Product, onBack: () -> Unit) {
    var recipientName by remember { mutableStateOf("") }
    var recipientPhone by remember { mutableStateOf("") }
    var recipientAddress by remember { mutableStateOf("") }
    var messageToShop by remember { mutableStateOf("") }

    val context = LocalContext.current

    fun toastMsg(
        context: Context,
        msg: String
    ) {
        Toast.makeText(
            context,
            msg,
            Toast.LENGTH_SHORT
        ).show()
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
        // Sử dụng verticalScroll để có thể cuộn nội dung
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()), // Thêm khả năng cuộn
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
            OutlinedTextField(
                value = messageToShop,
                onValueChange = { messageToShop = it },
                label = { Text("Lời nhắn cho shop bán hàng") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Nút thanh toán
            Button(
                onClick = {
                    toastMsg(
                        context = context,
                        msg = "Đã xác nhận thanh toán!"
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                shape = RoundedCornerShape(6.dp),
            ) {
                Text("Thanh toán")
            }
        }
    }
}
