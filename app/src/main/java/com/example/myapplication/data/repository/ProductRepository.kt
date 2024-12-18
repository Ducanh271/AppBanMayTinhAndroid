package com.example.myapplication.data.repository

import com.example.myapplication.data.api.ProductApi
import com.example.myapplication.data.models.Product

class ProductRepository(private val api: ProductApi) {
    suspend fun getProducts(): List<Product> {
        val response = api.getProducts()  // Gọi API và nhận về ProductResponse
        return response.products  // Trả về danh sách sản phẩm
    }

    suspend fun searchProducts(keyword: String): List<Product> {
        val response = api.searchProducts(keyword)
        return response.products // Trả về danh sách sản phẩm từ phản hồi
    }

    suspend fun getCategories(): List<String> {
        return api.getCategories() // trả về danh sách categories
    }

    suspend fun getProductsByCategory(category: String): List<Product> {
        val response = api.getProductsByCategory(category)
        return response.products // trả về sản phẩm theo category
    }


    suspend fun getProductById(productId: String): Product {
        return api.getProductById(productId)
    }
}
