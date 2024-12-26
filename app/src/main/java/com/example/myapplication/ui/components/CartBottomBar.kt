package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
@Composable
fun CartBottomBar(totalPrice: Double, onCheckout: () -> Unit) {
    Surface(shadowElevation = 8.dp) { // Thay elevation bằng shadowElevation
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Tổng thanh toán: $totalPrice VNĐ", // Bỏ dấu ngoặc nhọn {}
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onCheckout,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Thanh toán")
            }
        }
    }
}
