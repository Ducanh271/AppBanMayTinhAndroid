package com.example.myapplication.ui.screens

import android.content.Context
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.models.OrderItemRequest
import com.example.myapplication.MainActivity
import com.example.myapplication.dataZalopay.Api.CreateOrder
import com.example.myapplication.utils.SharedPrefUtils
import com.example.myapplication.viewmodel.CartViewModel
import com.example.myapplication.viewmodel.OrderViewModel
import kotlinx.coroutines.launch
import vn.zalopay.sdk.ZaloPayError
import vn.zalopay.sdk.ZaloPaySDK
import vn.zalopay.sdk.listeners.PayOrderListener
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreenCart(
    viewModel: CartViewModel,
    orderViewModel: OrderViewModel, // Thêm OrderViewModel
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val context2 = LocalContext.current
    val cartState = viewModel.cartState.collectAsState().value
    val totalPrice = viewModel.calculateTotalPrice()
    val userId = SharedPrefUtils.getUserId(context) // Lấy userId từ SharedPreferences
    val scope = rememberCoroutineScope() // Sử dụng coroutine trong Composable

    var recipientName by remember { mutableStateOf("") }
    var recipientPhone by remember { mutableStateOf("") }
    var recipientAddress by remember { mutableStateOf("") }

    // Tổng giá tiền
//    val formattedTotalPrice = NumberFormat.getCurrencyInstance().format(totalPrice)
//    val amountVND = formattedTotalPrice.toInt() * 25480 // Giá sản phẩm doi sang vnd

    val formattedTotalPrice = NumberFormat.getCurrencyInstance().format(totalPrice)
    val numericString = formattedTotalPrice.replace(Regex("[^\\d]"), "") // Loại bỏ ký hiệu tiền tệ
    val amountVND = (totalPrice * 25480).toInt() // Chuyển đổi và tính toán


    //hàm chuyển định dạng tách số từ tiền
    fun formatCurrency(amount: Long): String {
        val formatter = NumberFormat.getInstance(Locale.US).apply {
            this.maximumFractionDigits = 0 // Không hiển thị số thập phân
            this.isGroupingUsed = true // Kích hoạt dấu phân cách nhóm
        }
        return formatter.format(amount).replace(",", ".") // Thay dấu "," bằng dấu cách
    }

    fun toastMsg(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thanh toán giỏ hàng") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            val formattedAmount = formatCurrency(amountVND.toLong())

            Text(
                text = "Tổng : $formattedAmount VND",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(0.dp))

            // Danh sách sản phẩm trong giỏ hàng
            Text(
                text = "Danh sách sản phẩm:",
                style = MaterialTheme.typography.titleMedium
            )
            cartState?.items?.forEach { cartItem ->
                val formattedPrice = NumberFormat.getCurrencyInstance().format(cartItem.price)
                Text(
                    text = "- ${cartItem.title} (Số lượng: ${cartItem.quantity}, Giá: $formattedPrice)",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Form nhập thông tin
            Text("Nhập thông tin thanh toán", style = MaterialTheme.typography.titleMedium)

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

            Spacer(modifier = Modifier.height(6.dp))


            // Nút "Thanh toán bằng ZaloPay"
//            Button(
//                onClick = {
//                    if (recipientName.isNotBlank() && recipientPhone.isNotBlank() && recipientAddress.isNotBlank()) {
//                        val amount = amountVND.toString()
//                        val orderApi = CreateOrder()
//
//                        try {
//                            val data = orderApi.createOrder(amount)
//                            val code = data.getString("return_code")
//
//                            if (code == "1") {
//                                val token = data.getString("zp_trans_token")
//                                if (token.isNullOrEmpty()) {
//                                    Toast.makeText(context, "Lỗi nhận token thanh toán", Toast.LENGTH_SHORT).show()
//                                    return@Button
//                                }
//
//                                ZaloPaySDK.getInstance().payOrder(
//                                    context as MainActivity,
//                                    token,
//                                    "demozpdk://app",
//                                    object : PayOrderListener {
//                                        override fun onPaymentSucceeded(result: String?, message: String?, zpTransToken: String?) {
//                                            Toast.makeText(context, "Thanh toán thành công!", Toast.LENGTH_SHORT).show()
//
//                                            // Đảm bảo sau khi thanh toán thành công mới gửi thông tin đơn hàng lên server và xóa giỏ hàng
//                                            if (userId != null) {
//                                                scope.launch {
//                                                    try {
//                                                        // Chuyển đổi từ CartItem sang OrderItem
//                                                        val orderItems = cartState?.items?.map { cartItem ->
//                                                            OrderItemRequest(
//                                                                productId = cartItem.productId,
//                                                                quantity = cartItem.quantity
//                                                            )
//                                                        } ?: emptyList()
//
//                                                        // Gửi thông tin đơn hàng lên server
//                                                        orderViewModel.handleCartCashOnDeliveryPayment(
//                                                            userId = userId,
//                                                            orderItems = orderItems,
//                                                            recipientPhone = recipientPhone,
//                                                            recipientAddress = recipientAddress,
//                                                            context = context,
//                                                        )
//
//                                                        // Xóa giỏ hàng sau khi đơn hàng đã được tạo thành công
//                                                        viewModel.clearCart(userId)
//                                                    } catch (e: Exception) {
//                                                        Toast.makeText(context, "Đặt hàng thất bại: ${e.message}", Toast.LENGTH_SHORT).show()
//                                                    }
//                                                }
//                                            }
//                                        }
//
//                                        override fun onPaymentCanceled(p0: String?, p1: String?) {
//                                            Toast.makeText(context, "Thanh toán bị hủy!", Toast.LENGTH_SHORT).show()
//                                        }
//
//                                        override fun onPaymentError(error: ZaloPayError?, message: String?, description: String?) {
//                                            Toast.makeText(context, "Lỗi thanh toán: $message", Toast.LENGTH_SHORT).show()
//                                        }
//                                    }
//                                )
//                            } else {
//                                Toast.makeText(context, "Lỗi tạo đơn hàng: $code", Toast.LENGTH_SHORT).show()
//                            }
//
//                        } catch (e: Exception) {
//                            Toast.makeText(context, "Lỗi kết nối: ${e.message}", Toast.LENGTH_SHORT).show()
//                        }
//                    } else {
//                        Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
//                    }
//                },
//                modifier = Modifier.fillMaxWidth().height(45.dp),
//                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)), // Màu xanh cho ZaloPay
//                shape = RoundedCornerShape(6.dp),
//            ) {
//                Text(
//                    text = "Thanh toán bằng ZaloPay",
//                    fontSize = 17.sp,
//                    color = Color.White
//                )
//            }




            // Nút "Thanh toán khi nhận hàng"
            Button(
                onClick = {
                    if (recipientName.isNotEmpty() && recipientPhone.isNotEmpty() && recipientAddress.isNotEmpty()) {
                        if (userId != null) {
                            scope.launch {
                                try {
                                    // Chuyển đổi từ CartItem sang OrderItem
                                    val orderItems = cartState?.items?.map { cartItem ->
                                        OrderItemRequest(
                                            productId = cartItem.productId, // Hoặc thuộc tính tương ứng của OrderItem
                                            quantity = cartItem.quantity
                                        )
                                    } ?: emptyList()

                                    // Gửi thông tin đơn hàng lên server
                                    orderViewModel.handleCartCashOnDeliveryPayment(
                                        userId = userId,
                                        orderItems = orderItems, // Đây là List<OrderItem>
                                        recipientPhone = recipientPhone,
                                        recipientAddress = recipientAddress,
                                        context = context2,
                                    )

                                    //xoa don hang kieu gi
                                    userId?.let {
                                        viewModel.clearCart(it)
                                    }

                                } catch (e: Exception) {
                                    toastMsg(context2, "Đặt hàng thất bại: ${e.message}")
                                }
                            }
                        } else {
                            toastMsg(context2, "Vui lòng đăng nhập trước!")
                        }
                    } else {
                        toastMsg(context2, "Vui lòng nhập đầy đủ thông tin!")
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
