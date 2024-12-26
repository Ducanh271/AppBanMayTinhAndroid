package com.example.myapplication.ui.components

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.models.AddToCartRequest
import com.example.myapplication.data.models.Product
import com.example.myapplication.utils.SharedPrefUtils
import com.example.myapplication.viewmodel.CartViewModel
@Composable
fun QuantityDialog(
    product: Product,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var quantity by remember { mutableStateOf(1) } // Trạng thái lưu số lượng

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                onConfirm(quantity) // Gọi callback khi người dùng nhấn "Xác nhận"
                onDismiss() // Đóng dialog sau khi xử lý
            }) {
                Text("Xác nhận")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Hủy")
            }
        },
        title = {
            Text(text = "Chọn số lượng")
        },
        text = {
            Column {
                Text(text = product.title)
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = { if (quantity > 1) quantity-- }) {
                        Text("-")
                    }
                    Text(
                        text = quantity.toString(),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Button(onClick = { quantity++ }) {
                        Text("+")
                    }
                }
            }
        }
    )
}
