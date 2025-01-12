package com.example.myapplication.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.data.models.OrderItemResponse
import java.text.NumberFormat
import java.util.*

@Composable
fun OrderDetailItem(
    orderItem: OrderItemResponse
) {
    // Định dạng giá tiền với đơn vị VND
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    val priceFormatted = currencyFormat.format(orderItem.price)
    val totalFormatted = currencyFormat.format(orderItem.total)

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ảnh sản phẩm với bo tròn
            Image(
                painter = rememberAsyncImagePainter(orderItem.image),
                contentDescription = orderItem.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .padding(4.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Tên sản phẩm
                Text(
                    text = orderItem.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Giá sản phẩm và số lượng
                Text(
                    text = "Giá: $priceFormatted x ${orderItem.quantity}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Tổng giá
                Text(
                    text = "Tổng: $totalFormatted",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}
