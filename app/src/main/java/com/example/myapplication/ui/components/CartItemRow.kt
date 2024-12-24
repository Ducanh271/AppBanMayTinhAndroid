package com.example.myapplication.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.R
import com.example.myapplication.data.models.CartItem
import com.example.myapplication.data.models.Product

@Composable
fun ShopeeCartItemRow(
    product: Product,
    cartItem: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit,

) {
    var quantity by remember { mutableStateOf(cartItem.quantity) }
    var isItemSelected by remember { mutableStateOf(true) } // Placeholder for item selection

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isItemSelected,
                onCheckedChange = { isItemSelected = it }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painter = rememberAsyncImagePainter(product.image),
                contentDescription = product.title,
                modifier = Modifier.size(60.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Sản phẩm ${cartItem.productId}", // Thay thế bằng tên sản phẩm thật
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Text(text = "₫ 10.000", fontSize = 14.sp) // Thay thế bằng giá sản phẩm thật
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = { if (quantity > 1) { quantity--; onQuantityChange(quantity) } }) {
                        Text("-")
                    }
                    Text(text = quantity.toString(), fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    Button(onClick = { quantity++; onQuantityChange(quantity) }) {
                    }
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onRemove) {
                Icon(Icons.Filled.Close, contentDescription = "Xóa", tint = Color.Gray)
            }
        }
    }
}
