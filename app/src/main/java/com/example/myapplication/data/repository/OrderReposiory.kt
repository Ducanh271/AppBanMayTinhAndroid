package com.example.myapplication.data.repository

import OrderApi  // Đảm bảo rằng bạn đang import đúng lớp OrderApi từ đúng package
import com.example.myapplication.data.models.*
import retrofit2.Response

class OrdersRepository(private val orderApi: OrderApi) {

    // Tạo đơn hàng
    suspend fun createOrder(orderRequest: CreateOrderRequest): Response<CreateOrderResponse> {
        return try {
            orderApi.createOrder(orderRequest)
        } catch (e: Exception) {
            throw Exception("Error creating order: ${e.message}")
        }
    }

    // Lấy danh sách đơn hàng theo ID người dùng
    suspend fun getOrdersByUserId(userId: String): List<OrderResponse> {
        return try {
            orderApi.getOrdersByUserId(userId)
        } catch (e: Exception) {
            throw Exception("Error fetching orders for user $userId: ${e.message}")
        }
    }

    // Lấy chi tiết một đơn hàng
    suspend fun getOrderDetails(orderId: String): OrderResponse {
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
