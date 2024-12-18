package com.example.myapplication.data.api

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Path

import com.example.myapplication.data.models.ProductResponse
import com.example.myapplication.data.models.Product
interface ProductApi {
    @GET("api/products/{id}")
    suspend fun getProductById(@Path("id") productId: String): Product

    @GET("api/products") // Endpoint lấy danh sách sản phẩm
    suspend fun getProducts(): ProductResponse

    @GET("api/products/search") // Endpoint tìm kiếm sản phẩm
    suspend fun searchProducts(
        @Query("query") keyword: String // Tham số từ khóa tìm kiếm
    ): ProductResponse

    @GET("api/products/categories") // API lấy danh sách categories
    suspend fun getCategories(): List<String>

    @GET("api/products/category/{categoryName}") // API lấy sản phẩm theo category
    suspend fun getProductsByCategory(@Path("categoryName") categoryName: String): ProductResponse
}
