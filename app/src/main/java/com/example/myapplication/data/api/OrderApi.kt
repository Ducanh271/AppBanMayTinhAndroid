import com.example.myapplication.data.models.ApiResponse
import com.example.myapplication.data.models.OrderResponse
import com.example.myapplication.data.models.CreateOrderResponse
import com.example.myapplication.data.models.CreateOrderRequest
import com.example.myapplication.data.models.DetailOrderResponse
import com.example.myapplication.data.models.Product

import com.example.myapplication.data.models.OrderItemRequest
import com.example.myapplication.data.models.ProductResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface OrderApi {
    // Tạo đơn hàng
    @POST("/api/orders/")
    suspend fun createOrder(@Body orderRequest: CreateOrderRequest): Response<CreateOrderResponse>

    // Lấy danh sách đơn hàng theo ID người dùng
    @GET("/api/orders/user/{userId}")
    suspend fun getOrdersByUserId(@Path("userId") userId: String): List<OrderResponse>

    // Lấy chi tiết đơn hàng
    @GET("/api/order/{orderId}")
    suspend fun getOrderDetails(@Path("orderId") orderId: String): DetailOrderResponse

    // Hủy đơn hàng
    @DELETE("/order/{orderId}")
    suspend fun cancelOrder(@Path("orderId") orderId: String): ApiResponse

    //Lay All orderr
    @GET("/api/orders/") // Thay bằng endpoint thực tế của bạn
    suspend fun getAllOrders(): List<OrderResponse>

    @GET("products/{id}")
    suspend fun getProductDetails(@Path("id") productId: String): Product
}


