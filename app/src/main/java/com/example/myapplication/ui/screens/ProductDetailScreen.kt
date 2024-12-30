package com.example.myapplication.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.data.models.Product
import com.example.myapplication.ui.components.QuantityDialog
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    product: Product,
    onBack: () -> Unit,
    onBuyNow: (Product) -> Unit,
    onAddToCart: (Product, Int) -> Unit, // Callback để xử lý thêm sản phẩm vào giỏ hàng
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf("") } // Trạng thái để lưu thông báo
    val amountVND = product.price.toInt() * 25480 // Giá sản phẩm doi sang vnd

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
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun formatCurrency(amount: Long): String {
        val formatter = NumberFormat.getInstance(Locale.US).apply {
            this.maximumFractionDigits = 0 // Không hiển thị số thập phân
            this.isGroupingUsed = true // Kích hoạt dấu phân cách nhóm
        }
        return formatter.format(amount).replace(",", ".") // Thay dấu "," bằng dấu cách
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(product.title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Nội dung chính hiển thị thông tin sản phẩm
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 80.dp) // Đệm dưới để tránh nút
            ) {
                item {
                    // 1. Ảnh sản phẩm
                    Image(
                        painter = rememberAsyncImagePainter(product.image),
                        contentDescription = product.title,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                    )
                }
                item {
                    // 2. Thông tin sản phẩm
                    val formattedAmount = formatCurrency(amountVND.toLong()) //chuyen doi gia sang VND
                    Text(
                        text = product.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Giá sản phẩm",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${formattedAmount} VND",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                item {
                    // 3. Đánh giá và mô tả
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⭐⭐⭐⭐⭐", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("4.9 (1234 đánh giá)", style = MaterialTheme.typography.bodyMedium)
                    }
                    Divider()
                    Text(
                        text = "Mô tả sản phẩm",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Text(
                        text = product.description,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            // Nút "Mua ngay" và "Thêm vào giỏ hàng" được đặt cố định ở cuối màn hình
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter) // Căn dưới cùng
                    .padding(16.dp) // Khoảng cách mép ngoài
            ) {
                Button(
                    onClick = { onBuyNow(product) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Mua ngay")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Thêm vào giỏ hàng")
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
}
