package com.example.myapplication.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.models.OrderItem
import com.example.myapplication.data.models.OrderRequest
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
        val orderRequest = OrderRequest(
            userId = userId,
            items = listOf(OrderItem(productId = product.id, quantity = 1)),
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
