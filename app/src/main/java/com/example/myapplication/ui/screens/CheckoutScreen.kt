package com.example.myapplication.ui.screens

import OrderApi
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.data.api.ApiService
import com.example.myapplication.data.models.OrderItemRequest
import com.example.myapplication.data.models.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.MainActivity
import com.example.myapplication.data.repository.AuthRepository
import com.example.myapplication.dataZalopay.Api.CreateOrder
import com.example.myapplication.utils.SharedPrefUtils
import com.example.myapplication.viewmodel.AuthViewModel
import com.example.myapplication.viewmodel.OrderViewModel
import vn.zalopay.sdk.ZaloPayError
import vn.zalopay.sdk.ZaloPaySDK
import vn.zalopay.sdk.listeners.PayOrderListener
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    orderViewModel: OrderViewModel,
    product: Product,
    onBack: () -> Unit
) {
    var recipientName by remember { mutableStateOf("") }
    var recipientPhone by remember { mutableStateOf("") }
    var recipientAddress by remember { mutableStateOf("") }
    val context = LocalContext.current
    val context2 = LocalContext.current
    val userId = SharedPrefUtils.getUserId(context)
    val amountVND = product.price.toInt() * 25480 // Giá sản phẩm doi sang vnd


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
                title = { Text("Thanh toán") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp) // Khoảng cách giữa ảnh và thông tin
        ) {
            // 1. Ảnh sản phẩm bên trái
            Image(
                painter = rememberAsyncImagePainter(product.image),
                contentDescription = product.title,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(150.dp) // Kích thước ảnh vuông
            )

            // 2. Thông tin sản phẩm bên phải
            Column(
                modifier = Modifier.weight(1f) // Chiếm không gian còn lại
            ) {
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tổng thanh toán",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.fillMaxWidth()
                )
                val formattedAmount = formatCurrency(amountVND.toLong()) //Chuyen doi gia USD to VND
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${formattedAmount} VND",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {


            Spacer(modifier = Modifier.height(200.dp))
            //Khoảng cách cho hiển thi row ảnh với tên sản phẩm bên trên
            // 3. Nhập thông tin thanh toán
            Text("Nhập thông tin địa chỉ thanh toán", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = recipientName,
                onValueChange = { recipientName = it },
                label = { Text("Họ và Tên người nhận") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = recipientPhone,
                onValueChange = { recipientPhone = it },
                label = { Text("Số điện thoại người nhận") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = recipientAddress,
                onValueChange = { recipientAddress = it },
                label = { Text("Địa chỉ nhận hàng") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(50.dp))

            // Nút "Thanh toán bằng ZaloPay"
            Button(
                onClick = {
                    if (recipientName.isNotBlank() && recipientPhone.isNotBlank() && recipientAddress.isNotBlank()) {
                        val amount = amountVND.toString()
                        val orderApi = CreateOrder()

                        try {
                            val data = orderApi.createOrder(amount) // Tạo đơn hàng với số tiền
                            val code = data.getString("return_code")

                            if (code == "1") {
                                val token = data.getString("zp_trans_token")

                                // Gọi hàm thanh toán ZaloPay
                                ZaloPaySDK.getInstance().payOrder(
                                    context as MainActivity,
                                    token,
                                    "demozpdk://app",
                                    object : PayOrderListener {
                                        override fun onPaymentSucceeded(
                                            result: String?,
                                            message: String?,
                                            zpTransToken: String?
                                        ) {
                                            Toast.makeText(
                                                context,
                                                "Thanh toán thành công!",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            if(userId != null){
                                                orderViewModel.handleCashOnDeliveryPayment(userId, product, recipientAddress, recipientPhone,context)
                                                //dat don hang thanh cong thi toast len thong tin dat don hang kem ID
                                            }

                                        }

                                        override fun onPaymentCanceled(p0: String?, p1: String?) {
                                            Toast.makeText(
                                                context,
                                                "Thanh toán bị hủy!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                        override fun onPaymentError(
                                            error: ZaloPayError?,
                                            message: String?,
                                            description: String?
                                        ) {
                                            Toast.makeText(
                                                context,
                                                "Lỗi thanh toán: $message",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                )
                            } else {
                                Toast.makeText(context, "Lỗi tạo đơn hàng: $code", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Lỗi kết nối: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(45.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)), // Màu xanh cho ZaloPay
                shape = RoundedCornerShape(6.dp),
            ) {
                Text(
                    text = "Thanh toán bằng ZaloPay",
                    fontSize = 17.sp,
                    color = Color.White
                )
            }


            //Button thanh toan khi nhan hang

            Button(
                onClick = {
                    if (recipientName.isNotEmpty() && recipientPhone.isNotEmpty() && recipientAddress.isNotEmpty()) {
                        if (userId != null) {  // Kiểm tra nếu userId đã có
                            orderViewModel.handleCashOnDeliveryPayment(userId, product, recipientAddress, recipientPhone,context2)
                            //khong dung chung context với nút thanh toa zalopay vì bị Bad Request?
                        } else {
                            Toast.makeText(context2, "Vui lòng đăng nhập trước!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context2, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(45.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                shape = RoundedCornerShape(6.dp),
            ) {
                Text(text = "Thanh toán khi nhận hàng", fontSize = 17.sp, color = Color.White)
            }

        }
    }
}
