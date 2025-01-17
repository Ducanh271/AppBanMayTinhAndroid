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
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    orderViewModel: OrderViewModel,
    product: Product,
    onBack: () -> Unit,
    onNavigateToOrderDetail: (String) -> Unit
) {
    var recipientName by remember { mutableStateOf("") }
    var recipientPhone by remember { mutableStateOf("") }
    var recipientAddress by remember { mutableStateOf("") }
    val context = LocalContext.current
    val userId = SharedPrefUtils.getUserId(context)

    // Trạng thái
    var isProcessing by remember { mutableStateOf(false) }
    var showCountdown by remember { mutableStateOf(false) }
    var countdownTime by remember { mutableStateOf(3) }
    var savedOrderId by remember { mutableStateOf<String?>(null) } // Lưu `orderId`

    // Hàm đếm ngược và chuyển hướng
    if (showCountdown) {
        LaunchedEffect(countdownTime) {
            if (countdownTime > 0) {
                kotlinx.coroutines.delay(1000L)
                countdownTime -= 1
            } else {
                showCountdown = false
                savedOrderId?.let { onNavigateToOrderDetail(it) } // Điều hướng với `orderId`
            }
        }
    }

    // Hàm định dạng tiền tệ
    fun formatCurrency(amount: Double): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        return formatter.format(amount)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thanh toán") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
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
            // Thông tin sản phẩm
            ProductInfo(product = product, formatCurrency = ::formatCurrency)

            // Nhập thông tin thanh toán
            PaymentInfo(
                recipientName = recipientName,
                recipientPhone = recipientPhone,
                recipientAddress = recipientAddress,
                onNameChange = { recipientName = it },
                onPhoneChange = { recipientPhone = it },
                onAddressChange = { recipientAddress = it }
            )

            // Hiển thị đếm ngược
            if (showCountdown) {
                CountdownDisplay(countdownTime)
            }

            Spacer(modifier = Modifier.height(16.dp))

//            // Nút "Thanh toán khi nhận hàng"
//            Button(
//                onClick = {
//                    if (recipientName.isNotBlank() && recipientPhone.isNotBlank() && recipientAddress.isNotBlank()) {
//                        userId?.let {
//                            orderViewModel.handleCashOnDeliveryPayment(
//                                userId = it,
//                                product = product,
//                                recipientAddress = recipientAddress,
//                                recipientPhone = recipientPhone,
//                                context = context,
//                                onSuccess = { orderId ->
//                                    recipientName = ""
//                                    recipientPhone = ""
//                                    recipientAddress = ""
//                                   // product.price = 0.00
//                                    savedOrderId = orderId // Lưu `orderId`
//                                    showCountdown = true
//                                    countdownTime = 3
//                                },
//                                onError = {
//                                    Toast.makeText(context, "Đặt hàng thất bại!", Toast.LENGTH_SHORT).show()
//                                }
//                            )
//                        } ?: Toast.makeText(context, "Vui lòng đăng nhập trước!", Toast.LENGTH_SHORT).show()
//                    } else {
//                        Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
//                    }
//                },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(50.dp),
//                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
//                shape = RoundedCornerShape(6.dp)
//            ) {
//                Text("Đặt đơn hàng", fontSize = 17.sp, color = Color.White)
//            }

            //nút thanh toán đặt hàng neww

            // Kiểm tra nếu thông tin đã đầy đủ
            var showConfirmationDialog by remember { mutableStateOf(false) } // Trạng thái hiển thị hộp thoại
            var isProcessing by remember { mutableStateOf(false) }
            val isFormValid = recipientName.isNotBlank() && recipientPhone.isNotBlank() && recipientAddress.isNotBlank()

            // Hiển thị hộp thoại xác nhận
            if (showConfirmationDialog) {
                AlertDialog(
                    onDismissRequest = { showConfirmationDialog = false }, // Đóng hộp thoại khi nhấn ngoài
                    title = { Text("Xác nhận đặt hàng?") },
                    text = { Text("Bạn có chắc chắn muốn đặt đơn hàng này?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                // Khi nhấn "Yes", tiếp tục quá trình đặt hàng
                                if (isFormValid) {
                                    userId?.let {
                                        orderViewModel.handleCashOnDeliveryPayment(
                                            userId = it,
                                            product = product,
                                            recipientAddress = recipientAddress,
                                            recipientPhone = recipientPhone,
                                            context = context,
                                            onSuccess = { orderId ->
                                                recipientName = ""
                                                recipientPhone = ""
                                                recipientAddress = ""
                                                savedOrderId = orderId // Lưu `orderId`
                                                showCountdown = true
                                                countdownTime = 3
                                            },
                                            onError = {
                                                Toast.makeText(context, "Đặt hàng thất bại!", Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                    } ?: Toast.makeText(context, "Vui lòng đăng nhập trước!", Toast.LENGTH_SHORT).show()
                                }
                                showConfirmationDialog = false // Đóng hộp thoại
                            }
                        ) {
                            Text("Yes")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                // Khi nhấn "No", chỉ đóng hộp thoại mà không làm gì thêm
                                showConfirmationDialog = false
                            }
                        ) {
                            Text("No")
                        }
                    }
                )
            }

            // Nút "Thanh toán khi nhận hàng"
            Button(
                onClick = {
                    // Kiểm tra thông tin trước khi hiển thị hộp thoại xác nhận
                    if (isFormValid) {
                        // Hiển thị hộp thoại xác nhận khi thông tin đầy đủ
                        showConfirmationDialog = true
                    } else {
                        // Nếu thông tin chưa đầy đủ, hiển thị thông báo
                        Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text("Đặt đơn hàng", fontSize = 17.sp, color = Color.White)
            }



            // Nút "Thanh toán bằng ZaloPay"
            Button(
                onClick = {
                    if (recipientName.isNotBlank() && recipientPhone.isNotBlank() && recipientAddress.isNotBlank()) {
                        val orderApi = CreateOrder()
                        try {
                            val data = orderApi.createOrder(product.price.toInt().toString())
                            val code = data.getString("return_code")
                            if (code == "1") {
                                val token = data.getString("zp_trans_token")
                                ZaloPaySDK.getInstance().payOrder(
                                    context as MainActivity,
                                    token,
                                    "demozpdk://app",
                                    object : PayOrderListener {
                                        override fun onPaymentSucceeded(result: String?, message: String?, zpTransToken: String?) {
                                            Toast.makeText(context, "Thanh toán thành công!", Toast.LENGTH_SHORT).show()
                                            userId?.let {
                                                orderViewModel.handleCashOnDeliveryPayment(
                                                    userId = it,
                                                    product = product,
                                                    recipientAddress = recipientAddress,
                                                    recipientPhone = recipientPhone,
                                                    context = context,
                                                    onSuccess = { orderId ->
                                                        recipientName = ""
                                                        recipientPhone = ""
                                                        recipientAddress = ""
                                                        // product.price = 0.00
                                                        savedOrderId = orderId // Lưu `orderId`
                                                        showCountdown = true
                                                        countdownTime = 3
                                                    },
                                                    onError = {
                                                        Toast.makeText(context, "Đặt hàng thất bại!", Toast.LENGTH_SHORT).show()
                                                    }
                                                )
                                            }
                                        }
                                        override fun onPaymentCanceled(p0: String?, p1: String?) {
                                            Toast.makeText(context, "Thanh toán bị hủy!", Toast.LENGTH_SHORT).show()
                                        }
                                        override fun onPaymentError(error: ZaloPayError?, message: String?, description: String?) {
                                            Toast.makeText(context, "Lỗi thanh toán: $message", Toast.LENGTH_SHORT).show()
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                shape = RoundedCornerShape(6.dp)

            ) {
                Text("Thanh toán bằng ZaloPay", fontSize = 17.sp, color = Color.White)
            }


        }
    }
}

@Composable
fun ProductInfo(product: Product, formatCurrency: (Double) -> String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(product.image),
            contentDescription = product.title,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(150.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = product.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Tổng thanh toán", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatCurrency(product.price),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun PaymentInfo(
    recipientName: String,
    recipientPhone: String,
    recipientAddress: String,
    onNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onAddressChange: (String) -> Unit
) {
    // Biến để kiểm tra hợp lệ số điện thoại
    var isPhoneValid by remember { mutableStateOf(true) }

    // Hàm kiểm tra hợp lệ số điện thoại
    fun validatePhoneNumber(phone: String): Boolean {
        val regex = Regex("^((\\+84)\\d{9}|\\d{10,12})$")
        return phone.matches(regex)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), // Đặt padding cho toàn bộ Column
        verticalArrangement = Arrangement.spacedBy(16.dp) // Khoảng cách đều giữa các hộp nhập
    ) {
        Text("Nhập thông tin địa chỉ thanh toán", style = MaterialTheme.typography.titleMedium)

        // Nhập họ tên
        OutlinedTextField(
            value = recipientName,
            onValueChange = onNameChange,
            label = { Text("Họ và Tên người nhận") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(1.dp))

        // Nhập số điện thoại
        OutlinedTextField(
            value = recipientPhone,
            onValueChange = { newPhone ->
                onPhoneChange(newPhone)
                // Kiểm tra hợp lệ nếu số điện thoại không trống
                isPhoneValid = newPhone.isEmpty() || validatePhoneNumber(newPhone)
            },
            label = { Text("Số điện thoại người nhận") },
            isError = !isPhoneValid, // Đánh dấu lỗi nếu không hợp lệ
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                if (!isPhoneValid && recipientPhone.isNotEmpty()) {
                    Text("Số điện thoại không hợp lệ", color = Color.Red)
                }
            }
        )

        // Nhập địa chỉ nhận hàng
        OutlinedTextField(
            value = recipientAddress,
            onValueChange = onAddressChange,
            label = { Text("Địa chỉ nhận hàng") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}




@Composable
fun CountdownDisplay(countdownTime: Int) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(
            text = "Đặt hàng thành công !!!",
            color = Color.Green,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Đang chuyển hướng sau $countdownTime giây...",
            color = Color.Green,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
