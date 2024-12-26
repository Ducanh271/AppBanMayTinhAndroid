package com.example.myapplication.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.myapplication.viewmodel.CartViewModel
import com.example.myapplication.data.models.CartItem
import com.example.myapplication.ui.components.CartItemRow
import android.util.Log
import com.example.myapplication.utils.LocalUserId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(viewModel: CartViewModel, onBack: () -> Unit) {

    val userId = LocalUserId.current // Lấy UserID từ CompositionLocal

    LaunchedEffect(Unit) {
        userId?.let {
            viewModel.loadCart(it)
        } ?: run {
            // Xử lý khi userId không tồn tại
                    Log.e("CartScreen", "User ID is null. Cannot load cart.")
        }
    }

    val cartState = viewModel.cartState.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Cart") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                cartState?.let { cart ->
                    LazyColumn {
                        items(cartState.items) { cartItem ->
                            CartItemRow(
                                cartItem = cartItem,
                                onRemove = {
                                    if (userId != null) {
                                        viewModel.removeCartItem(userId, cartItem.productId)
                                    } else {
                                        println("Error: User ID is null.")
                                    }
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "Total Price: $${cart.totalPrice}", style = MaterialTheme.typography.bodyLarge)

                    Button(
                        onClick = { /* Checkout logic */ },
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp)
                    ) {
                        Text(text = "Checkout")
                    }
                } ?: Text("Your cart is empty.")
            }
        }
    }
}
