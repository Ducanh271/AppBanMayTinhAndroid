package com.example.myapplication.viewmodel

import OrderApi
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.models.Cart
import com.example.myapplication.data.models.OrderItemRequest
import com.example.myapplication.data.models.CreateOrderRequest
import com.example.myapplication.data.models.OrderResponse
import com.example.myapplication.data.models.Product
import com.example.myapplication.data.repository.OrdersRepository
import com.example.myapplication.data.repository.ProductRepository
import com.example.myapplication.utils.SharedPrefUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.Thread.State

class OrderViewModel(private val ordersRepository: OrdersRepository,
                     private val userId: String // Truyền userId từ bên ngoài
) : ViewModel() {

    //code cho OderScreen:
    // StateFlow cho danh sách đơn hàng
    private val _orderList = MutableStateFlow<List<OrderResponse>>(emptyList())
    val orderList: StateFlow<List<OrderResponse>> = _orderList

    private val _orderItemList = MutableStateFlow<OrderResponse?>(null)
    val orderItemList: StateFlow<OrderResponse?> = _orderItemList

    private val _message = MutableStateFlow<String>("")
    val message: StateFlow<String> = _message
    // StateFlow cho trạng thái tải dữ liệu
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Hàm lấy danh sách orders
    init {
        fetchOrders(userId) // Truyền userId khi gọi fetchOrders
    }

    fun fetchOrders(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = ordersRepository.getOrdersByUserId(userId)
                _orderList.value = response
            } catch (e: Exception) {
                _orderList.value = emptyList() // Lỗi, không có đơn hàng
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun loadOrder(orderId: String) {
        Log.d("OrderViewModel", "loadOrder called with ID: $orderId")
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = ordersRepository.getOrderDetails(orderId)
                _orderItemList.value = response
                Log.d("OrderViewModel", "Order details loaded: $response")
            } catch (e: Exception) {
                Log.e("OrderViewModel", "Error loading order: ${e.message}") // Log lỗi
                _message.value = "Failed to load order: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }


    //Petch Product order


    fun handleCashOnDeliveryPayment(
        userId: String,
        product: Product,
        recipientAddress: String,
        recipientPhone: String,
        context: Context
    ) {
        val orderRequest = CreateOrderRequest(
            userId = userId,
            items = listOf(OrderItemRequest(productId = product.id, quantity = 1)),
            address = recipientAddress,
            phoneNumber = recipientPhone
        )

        viewModelScope.launch {
            try {
                val response = ordersRepository.createOrder(orderRequest)
                if (response.isSuccessful) {
                    Toast.makeText(context, "Đặt hàng thành công! Mã đơn: ${response.body()?.orderId}", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Đặt hàng thất bại: ${response.message()}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Lỗi mạng: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }


    //handleCassh zalo pay rieng
    fun handleCashOnDeliveryPayment_ZaloPay(
        userId: String,
        product: Product,
        recipientAddress: String,
        recipientPhone: String,
        context: Context
    ) {
        val orderRequest = CreateOrderRequest(
            userId = userId,
            items = listOf(OrderItemRequest(productId = product.id, quantity = 1)),
            address = recipientAddress,
            phoneNumber = recipientPhone
        )

        viewModelScope.launch {
            try {
                val response = ordersRepository.createOrder(orderRequest)
                if (response.isSuccessful) {
                    Toast.makeText(context, "Đặt hàng thành công! Mã đơn: ${response.body()?.orderId}", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Đặt hàng thất bại: ${response.message()}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Lỗi mạng: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }



    //   try new code thanh toan nhieu san pham tu gio hang

    fun handleCartCashOnDeliveryPayment(
        userId: String,
        recipientAddress: String,
        recipientPhone: String,
        context: Context,
        orderItems: List<OrderItemRequest>
    ) {
        val orderRequest = CreateOrderRequest(
            userId = userId,
            items = orderItems, // Danh sách sản phẩm từ giỏ hàng
            address = recipientAddress,
            phoneNumber = recipientPhone
        )

        viewModelScope.launch {
            try {
                val response = ordersRepository.createOrder(orderRequest)
                if (response.isSuccessful) {
                    Toast.makeText(context, "Đặt hàng thành công! Mã đơn: ${response.body()?.orderId}", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Đặt hàng thất bại: ${response.message()}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Lỗi mạng: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }




}
