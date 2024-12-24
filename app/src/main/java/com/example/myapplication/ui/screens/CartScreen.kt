package com.example.myapplication.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.models.Cart
import com.example.myapplication.data.models.CartItem
import com.example.myapplication.ui.components.ShopeeCartItemRow
import com.example.myapplication.viewmodel.CartViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    userId: String,
    cartViewModel: CartViewModel

) {
    val cartState = cartViewModel.cart.collectAsState()
    val isLoading by cartViewModel.isLoading.collectAsState()
    val errorMessage by cartViewModel.errorMessage.collectAsState()

    // Gọi để lấy giỏ hàng khi màn hình được tạo
    LaunchedEffect(key1 = Unit) {
        cartViewModel.getCart(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Giỏ hàng") },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: Navigate back */ }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        },
        bottomBar = {
            CartBottomBar(
                cart = cartState.value,
                onCheckoutClicked = { /* TODO: Handle checkout */ }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Error: $errorMessage")
            }
        } else {
            val cart = cartState.value
            if (cart == null || cart.items.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Giỏ hàng trống")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    items(cart.items, key = { it.productId }) { cartItem ->
                        ShopeeCartItemRow(
                            cartItem = cartItem,
                            onQuantityChange = { newQuantity ->
                                cartViewModel.updateQuantity(userId, cartItem.productId, newQuantity)
                            },
                            onRemove = {
                                cartViewModel.removeItem(userId, cartItem.productId)
                            }
                        )
                        Divider()
                    }
                }
            }
        }
    }
}

@Composable
fun CartBottomBar(cart: Cart?, onCheckoutClicked: () -> Unit) {
    var isAllSelected by remember { mutableStateOf(true) } // Placeholder for "Select All" logic
    val totalPrice = cart?.items?.sumOf {
        // Placeholder for calculating total price based on product details
        it.quantity * 10000 // Example calculation, replace with actual logic
    } ?: 0

    Surface(elevation = 8.dp) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isAllSelected,
                        onCheckedChange = { isAllSelected = it },
                    )
                    Text("Chọn tất cả", fontSize = 16.sp)
                }
                Text("Tổng thanh toán: ${totalPrice} VNĐ", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onCheckoutClicked,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0C040)) // Shopee yellow
            ) {
                Text("Thanh toán", color = Color.Black, fontSize = 18.sp)
            }
        }
    }
}