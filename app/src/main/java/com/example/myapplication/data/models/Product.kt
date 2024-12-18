package com.example.myapplication.data.models
import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("_id") val id: String, // Ánh xạ "_id" từ JSON thành "id"
    val title: String,           // Tên sản phẩm
    val price: Double,           // Giá sản phẩm
    val description: String,     // Mô tả sản phẩm
    val category: String,        // Danh mục sản phẩm
    val image: String            // Đường dẫn hình ảnh
)