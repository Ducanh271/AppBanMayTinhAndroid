package com.example.myapplication.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.example.myapplication.data.models.AddToCartRequest
import com.example.myapplication.data.models.Product
import com.example.myapplication.ui.components.QuantityDialog
import com.example.myapplication.utils.SharedPrefUtils
import com.example.myapplication.viewmodel.CartViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    product: Product,
    onBack: () -> Unit,
    onBuyNow: (Product) -> Unit,
    cartViewModel: CartViewModel
) {
    var showDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Định dạng tiền tệ VND
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    val priceFormatted = currencyFormat.format(product.price)

    // Hàm thêm sản phẩm vào giỏ hàng
    fun onAddToCart(product: Product, quantity: Int) {
        val userId = SharedPrefUtils.getUserId(context)
        if (userId != null) {
            val request = AddToCartRequest(userId, product.id, quantity)
            cartViewModel.addToCart(request) { message ->
                snackbarMessage = message
            }
        } else {
            snackbarMessage = "Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng."
        }
    }

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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                item {
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
                    Text(
                        text = product.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Giá sản phẩm",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$priceFormatted",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                item {
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

            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter) // Đặt toàn bộ Column ở đáy và giữa màn hình
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(3.dp)
                        .height(70.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
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
                }
            }



            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
            LaunchedEffect(snackbarMessage) {
                if (snackbarMessage.isNotEmpty()) {
                    snackbarHostState.showSnackbar(snackbarMessage)
                    snackbarMessage = ""
                }
            }
        }
    }
}
