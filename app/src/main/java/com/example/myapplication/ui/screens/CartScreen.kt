package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.myapplication.viewmodel.CartViewModel
import com.example.myapplication.ui.components.CartItemRow
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.sp
import com.example.myapplication.utils.LocalUserId
import java.text.NumberFormat
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.example.myapplication.utils.SharedPrefUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: CartViewModel,
    onBack: () -> Unit,
    onCheckout: () -> Unit
) {
    val context = LocalContext.current
    val userId = SharedPrefUtils.getUserId(context)

    // Load cart when userId is available
    LaunchedEffect(userId) {
        userId?.let {
            viewModel.loadCart(it)
        } ?: run {
            Log.e("CartScreen", "User ID is null. Cannot load cart.")
        }
    }

    val cartState = viewModel.cartState.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value
    val totalPrice = viewModel.calculateTotalPrice()

    // Hàm định dạng tiền VND
    fun formatCurrency(amount: Double): String {
        val formatter = NumberFormat.getCurrencyInstance(java.util.Locale("vi", "VN"))
        return formatter.format(amount)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Giỏ hàng của bạn") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                cartState?.let { cart ->
                    // Danh sách sản phẩm trong giỏ hàng
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 100.dp), // Không gian để tránh chồng lấn với phần tổng tiền
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        items(cart.items) { cartItem ->
                            CartItemRow(
                                cartItem = cartItem,
                                onRemove = {
                                    userId?.let {
                                        viewModel.removeCartItem(it, cartItem.productId)
                                    } ?: Log.e("CartScreen", "User ID is null. Cannot remove item.")
                                },
                                onQuantityChange = { newQuantity ->
                                    userId?.let {
                                        viewModel.updateCartItem(it, cartItem.productId, newQuantity)
                                    } ?: Log.e("CartScreen", "User ID is null. Cannot update quantity.")
                                }
                            )
                        }
                    }

                    // Phần hiển thị tổng tiền và nút Thanh toán
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(16.dp)
                    ) {
                        val formattedTotalPrice = formatCurrency(totalPrice) // Định dạng giá trị tổng tiền

                        Text(
                            text = "Tổng số tiền: $formattedTotalPrice",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 25.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.align(Alignment.CenterHorizontally) // Căn giữa
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onCheckout,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Thanh toán", fontSize = 17.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                userId?.let {
                                    viewModel.clearCart(it)
                                } ?: Log.e("CartScreen", "User ID is null. Cannot clear cart.")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Xóa hết sản phẩm",
                                fontSize = 17.sp,
                                color = Color.White
                            )
                        }
                    }
                } ?: Text(
                    text = "Giỏ hàng của bạn trống.",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}
