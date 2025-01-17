package com.example.myapplication.data.api

import OrderApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiService {
    private const val BASE_URL = "http://192.168.97.36:3000/"
    // Đổi IP tương ứng trong mạng LAN

    // Tạo Retrofit instance chung
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // API cho Product
    val productApi: ProductApi by lazy {
        retrofit.create(ProductApi::class.java)
    }

    // API cho User
    val userApi: UserApi by lazy {
        retrofit.create(UserApi::class.java)
    }

    // API cho Cart
    val cartApi: CartApi by lazy {
        retrofit.create(CartApi::class.java)
    }

    //API cho Orders

    val orderApi: OrderApi by lazy {
        retrofit.create(OrderApi::class.java)
    }
}