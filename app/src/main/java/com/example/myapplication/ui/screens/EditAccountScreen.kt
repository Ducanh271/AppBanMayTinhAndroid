package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myapplication.utils.SharedPrefUtils
import com.example.myapplication.viewmodel.AuthViewModel

@Composable
fun EditAccountScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf(SharedPrefUtils.getUserName(context) ?: "") }
    var email by remember { mutableStateOf(SharedPrefUtils.getUserEmail(context) ?: "") }
    val userId = SharedPrefUtils.getUserId(context) ?: ""
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Chỉnh sửa thông tin cá nhân",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Tên") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Gọi hàm cập nhật thông tin người dùng trong ViewModel
                    authViewModel.updateUser(userId, name, email, context)
                navController.popBackStack() // Quay lại màn hình tài khoản
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Lưu thay đổi")
        }
    }
}
