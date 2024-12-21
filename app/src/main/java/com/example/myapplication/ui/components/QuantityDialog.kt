package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.models.Product

@Composable
fun QuantityDialog(
    product: Product,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit // Callback trả về số lượng đã chọn
) {
    var quantity by remember { mutableStateOf(1) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = { onConfirm(quantity) }) {
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
                    Text(text = quantity.toString(), style = MaterialTheme.typography.headlineSmall)
                    Button(onClick = { quantity++ }) {
                        Text("+")
                    }
                }
            }
        }
    )
}
