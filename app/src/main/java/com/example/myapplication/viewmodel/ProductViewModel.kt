package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.models.Product
import com.example.myapplication.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {
    // Danh sách sản phẩm
    private val _productList = MutableStateFlow<List<Product>>(emptyList())
    val productList: StateFlow<List<Product>> = _productList

    // Trạng thái loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    // chọn sản phẩm
    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct
    // Thông báo lỗi
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Danh sách categories
    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories
    val selectedCategory = MutableStateFlow("All")

    // Trạng thái trang và danh sách
    private var currentPage = 1

    fun loadNextPage() {
        if (_isLoading.value) return // Không tải nếu đang tải

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val products = repository.getProducts(currentPage, limit = 10)
                if (products.isNotEmpty()) {
                    _productList.value = _productList.value + products
                    currentPage++ // Tăng trang sau khi tải xong
                } else {
                    // Nếu không còn sản phẩm, có thể log hoặc xử lý trạng thái
                    println("No more products to load")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun fetchProducts(page: Int = 1, limit: Int = 10) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val products = repository.getProducts(page = page, limit = limit)
                _productList.value = products
                currentPage = page + 1 // Chuẩn bị cho trang tiếp theo
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                println("Error fetching products: ${e.message}") // Log lỗi
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchProducts(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _productList.value = repository.searchProducts(query)
            } catch (e: Exception) {
                _productList.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
    // Hàm lấy sản phẩm theo id
    fun fetchProductById(productId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _selectedProduct.value = null
            try {
                val product = repository.getProductById(productId)
                println("Product fetched successfully: $product") // Log kiểm tra
                _selectedProduct.value = product
            } catch (e: Exception) {
                println("Error fetching product by ID: ${e.message}") // Log lỗi
                _errorMessage.value = "Error fetching product: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    // Hàm lấy danh sách categories
    fun fetchCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _categories.value = repository.getCategories()
            } catch (e: Exception) {
                _errorMessage.value = "Error loading categories: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Hàm lấy sản phẩm theo category
    fun fetchProductsByCategory(category: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _productList.value = repository.getProductsByCategory(category)
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                _productList.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
