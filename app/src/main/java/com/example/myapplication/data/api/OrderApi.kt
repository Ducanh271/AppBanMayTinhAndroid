import com.example.myapplication.data.models.ApiResponse
import com.example.myapplication.data.models.Order
import com.example.myapplication.data.models.OrderDetails
import com.example.myapplication.data.models.OrderRequest
import com.example.myapplication.data.models.OrderResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface OrderApi {
    // Tạo đơn hàng
    @POST("/createOrder")
    suspend fun createOrder(@Body orderRequest: OrderRequest): Response<OrderResponse>

    // Lấy danh sách đơn hàng theo ID người dùng
    @GET("/orders/{userId}")
    suspend fun getOrdersByUserId(@Path("userId") userId: String): List<Order>

    // Lấy chi tiết đơn hàng
    @GET("/order/{orderId}")
    suspend fun getOrderDetails(@Path("orderId") orderId: String): OrderDetails

    // Hủy đơn hàng
    @DELETE("/order/{orderId}")
    suspend fun cancelOrder(@Path("orderId") orderId: String): ApiResponse
}


