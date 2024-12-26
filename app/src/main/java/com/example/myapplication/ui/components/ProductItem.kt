package com.example.myapplication.ui.components

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.data.models.AddToCartRequest
import com.example.myapplication.data.models.Product
import com.example.myapplication.utils.SharedPrefUtils
import com.example.myapplication.viewmodel.CartViewModel

@Composable
fun ProductItem(
    product: Product,
    onClick: (Product) -> Unit,
    onAddToCart: (Product, Int) -> Unit, // Callback để xử lý thêm sản phẩm vào giỏ hàng

) {
    var showDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf("") } // Trạng thái để lưu thông báo

    // Hiển thị dialog khi cần
    if (showDialog) {
        QuantityDialog(
            product = product,
            onDismiss = { showDialog = false },
            onConfirm = { quantity ->
                onAddToCart(product, quantity)
                snackbarMessage = "Đã thêm sản phẩm ${product.title} vào giỏ hàng"
                showDialog = false
            }
        )
    }

    // Giao diện chính
    Box {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    println("Product clicked: $product")
                    onClick(product)
                },
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Row(modifier = Modifier.padding(8.dp)) {
                Image(
                    painter = rememberAsyncImagePainter(product.image),
                    contentDescription = product.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${product.price} $",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Text("Add")
                }
            }
        }

        // Hiển thị Snackbar khi có thông báo
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
        LaunchedEffect(snackbarMessage) {
            if (snackbarMessage.isNotEmpty()) {
                snackbarHostState.showSnackbar(snackbarMessage)
                snackbarMessage = "" // Xóa thông báo sau khi hiển thị
            }
        }


    }
}
