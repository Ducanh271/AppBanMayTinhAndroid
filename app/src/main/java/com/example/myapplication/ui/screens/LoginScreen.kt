package com.example.myapplication.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myapplication.R
import com.example.myapplication.utils.AccountPreferences
import com.example.myapplication.viewmodel.AuthViewModel
import androidx.compose.foundation.clickable
import android.widget.Toast
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction


@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit
) {
    // Biến trạng thái cho email, password và hiển thị mật khẩu
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var showSaveAccountDialog by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }


    val loginState by viewModel.loginState.collectAsState()
    val context = LocalContext.current

    val accountPreferences = remember { AccountPreferences(context) }
    val suggestedAccounts = remember(email) {
        if (email.isNotEmpty()) {
            accountPreferences.getSavedAccounts().filter {
                it.email.startsWith(email, ignoreCase = true)
            }
        } else {
            emptyList()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp) // Padding chung cho toàn bộ màn hình
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .verticalScroll(rememberScrollState()), // Cho phép cuộn nếu nội dung dài
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.logoapp),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(100.dp) // Giảm kích thước logo để tạo thêm không gian
                    .padding(bottom = 24.dp) // Tạo khoảng cách dưới logo
            )

            // Tiêu đề
            Text(
                text = "Đăng nhập",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Danh sách gợi ý tài khoản (hiển thị phía trên trường nhập email)
            if (suggestedAccounts.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp) // Padding ngang để tránh sát lề
                        .padding(bottom = 16.dp) // Tạo khoảng cách dưới danh sách gợi ý
                ) {
                    suggestedAccounts.forEach { account ->
                        Text(
                            text = account.email,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    email = account.email
                                    password = account.password
                                }
                                .padding(vertical = 8.dp, horizontal = 8.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            }

            // Trường nhập Email
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = if (isValidEmail(email)) "" else "Vui lòng nhập đúng định dạng Email"
                },
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email Icon") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp) // Padding ngang để tránh sát lề
                    .padding(bottom = 8.dp), // Tạo khoảng cách dưới trường nhập
                isError = emailError.isNotEmpty()
            )
            if (emailError.isNotEmpty()) {
                Text(
                    text = emailError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            // Trường nhập Password
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = if (isValidPassword(password)) "" else "Mật khẩu phải có ít nhất 6 ký tự, bao gồm chữ hoa, chữ thường và số"
                },
                label = { Text("Mật khẩu") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password Icon") },
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle Password Visibility"
                        )
                    }
                },
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp) // Padding ngang để tạo không gian
                    .padding(bottom = 8.dp), // Tạo khoảng cách dưới trường nhập
                isError = passwordError.isNotEmpty()
            )
            // Hiển thị lỗi mật khẩu (nếu có)
            if (passwordError.isNotEmpty()) {
                Text(
                    text = passwordError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            // Nút Login
            Button(
                onClick = {
                    when {
                        !isValidEmail(email) -> {
                            Toast.makeText(context, "Vui lòng nhập đúng định dạng Email", Toast.LENGTH_SHORT).show()
                        }
                        !isValidPassword(password) -> {
                            Toast.makeText(context, "Mật khẩu phải có ít nhất 6 ký tự, bao gồm chữ hoa, chữ thường và số", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            viewModel.login(email, password, context)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .padding(horizontal = 8.dp) // Padding ngang cho nút
                    .padding(top = 16.dp)
            ) {
                Text("Đăng nhập", fontSize = 18.sp)
            }

            // Hiển thị lỗi nếu có
            loginState.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .padding(horizontal = 8.dp), // Padding ngang để tránh bị che
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Điều hướng đến màn hình đăng ký
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Bạn chưa có tài khoản?", color = Color.Gray)
                Spacer(modifier = Modifier.width(4.dp))
                TextButton(onClick = { navController.navigate("sign_up") }) {
                    Text("Đăng ký")
                }
            }

            // Loading indicator (nếu đang đăng nhập)
            if (loginState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .padding(horizontal = 8.dp) // Padding ngang để đồng nhất
                )
            }
        }

        // Hộp thoại lưu tài khoản (Save hoặc No Thanks)
        if (loginState.success != null && showSaveAccountDialog) {
            AlertDialog(
                onDismissRequest = { showSaveAccountDialog = false },
                title = { Text("Save Account?") },
                text = { Text("Would you like to save this account for faster login next time?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            accountPreferences.saveAccount(email, password)
                            showSaveAccountDialog = false
                            onLoginSuccess()
                        }
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showSaveAccountDialog = false
                            onLoginSuccess()
                        }
                    ) {
                        Text("No Thanks")
                    }
                }
            )
        }
        if (loginState.success != null && !showSaveAccountDialog) {
            LaunchedEffect(loginState.success) {
                onLoginSuccess()
            }
        }
    }
}