package com.example.myapplication.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.models.OrderItemRequest
import com.example.myapplication.data.models.CreateOrderRequest
import com.example.myapplication.data.models.Product
import com.example.myapplication.data.repository.OrdersRepository
import kotlinx.coroutines.launch

class OrderViewModel(private val ordersRepository: OrdersRepository) : ViewModel() {

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
