package com.example.myapplication.data.repository

import OrderApi  // Đảm bảo rằng bạn đang import đúng lớp OrderApi từ đúng package
import com.example.myapplication.data.models.*
import retrofit2.Response

class OrdersRepository(private val orderApi: OrderApi) {

    // Tạo đơn hàng
    suspend fun createOrder(orderRequest: OrderRequest): Response<OrderResponse> {
        return try {
            orderApi.createOrder(orderRequest)
        } catch (e: Exception) {
            throw Exception("Error creating order: ${e.message}")
        }
    }

    // Lấy thông tin đơn hàng theo ID người dùng
    suspend fun getOrdersByUserId(userId: String): List<Order> {
        return try {
            orderApi.getOrdersByUserId(userId)
        } catch (e: Exception) {
            throw Exception("Error fetching orders: ${e.message}")
        }
    }

    // Lấy chi tiết một đơn hàng
    suspend fun getOrderDetails(orderId: String): OrderDetails {
        return try {
            orderApi.getOrderDetails(orderId)
        } catch (e: Exception) {
            throw Exception("Error fetching order details: ${e.message}")
        }
    }

    // Hủy đơn hàng
    suspend fun cancelOrder(orderId: String): ApiResponse {
        return try {
            orderApi.cancelOrder(orderId)
        } catch (e: Exception) {
            throw Exception("Error cancelling order: ${e.message}")
        }
    }
}
