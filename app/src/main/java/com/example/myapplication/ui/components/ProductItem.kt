package com.example.myapplication.ui.components

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.data.models.Product
import com.example.myapplication.R
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ProductItem(
    product: Product,
    onClick: (Product) -> Unit,
    onAddToCart: (Product, Int) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf("") }

    // Hàm định dạng tiền VND
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    val priceFormatted = currencyFormat.format(product.price)

    if (showDialog) {
        QuantityDialog(
            product = product,
            onDismiss = { showDialog = false },
            onConfirm = { quantity ->
                onAddToCart(product, quantity)
                snackbarMessage = "Đã thêm ${product.title} vào giỏ hàng"
                showDialog = false
            }
        )
    }

    Card(
        modifier = Modifier
            .width(180.dp) // Đảm bảo chiều rộng đồng nhất cho tất cả các card
            .padding(8.dp)
            .clickable { onClick(product) },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally // Căn giữa nội dung theo chiều ngang
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = product.image,
                    placeholder = painterResource(R.drawable.placeholder_image),
                    error = painterResource(R.drawable.error_image)
                ),
                contentDescription = product.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth() // Căng ảnh theo chiều rộng
                    .height(180.dp) // Chiều cao cố định
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = product.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth() // Đảm bảo chiều rộng cố định để căn chỉnh đều
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Hiển thị giá bằng VND
                Text(
                    text = priceFormatted,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )

                IconButton(onClick = { showDialog = true }) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "Thêm vào giỏ hàng")
                }
            }
        }
    }

    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier.fillMaxWidth()
    )
    LaunchedEffect(snackbarMessage) {
        if (snackbarMessage.isNotEmpty()) {
            snackbarHostState.showSnackbar(snackbarMessage)
            snackbarMessage = ""
        }
    }
}
