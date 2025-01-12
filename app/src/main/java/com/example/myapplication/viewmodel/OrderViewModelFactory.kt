package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.data.repository.OrdersRepository

class OrderViewModelFactory(
    private val repository: OrdersRepository,
    private val userId: String // Thêm userId
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OrderViewModel(repository, userId) as T
    }
}
