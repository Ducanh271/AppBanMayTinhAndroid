package com.example.myapplication.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.data.models.Product

@Composable
fun ProductItem(
    product: Product,
    onClick: (Product) -> Unit, // Callback khi click vào sản phẩm
    onAddToCart: () -> Unit // Callback khi click "Add"
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                println("Product clicked: $product") // Log thông tin sản phẩm
                onClick(product) }, // Xử lý click vào sản phẩm
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            // Hiển thị ảnh sản phẩm
            Image(
                painter = rememberAsyncImagePainter(product.image),
                contentDescription = product.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))

            // Thông tin sản phẩm
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

            // Nút "Add"
            Button(
                onClick = onAddToCart,
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Text("Add")
            }
        }
    }
}
