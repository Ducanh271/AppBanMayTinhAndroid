package com.example.myapplication.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myapplication.R
import com.example.myapplication.utils.AccountPreferences
import com.example.myapplication.viewmodel.AuthViewModel
// Import statements ở đây...

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit
) {
    // Khởi tạo các biến và states
    val context = LocalContext.current
    val accountPreferences = remember { AccountPreferences(context) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showSuggestions by remember { mutableStateOf(false) }

    val loginState by viewModel.loginState.collectAsState()

    // UI chính
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.logoapp),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(120.dp)
                    .padding(16.dp)
            )

            // Tiêu đề Welcome
            Text(
                text = "Welcome Back!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Email field với suggestions
            Box {
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        if (it.isNotEmpty()) {
                            showSuggestions = true
                        }
                    },
                    label = { Text("Email") },
                    leadingIcon = { Icon(Icons.Default.Email, "Email Icon") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused && email.isNotEmpty()) {
                                showSuggestions = true
                            }
                        }
                )

                // Dropdown menu cho suggestions
                if (showSuggestions) {
                    val suggestions = accountPreferences.getSuggestedAccounts(email)
                    if (suggestions.isNotEmpty()) {
                        DropdownMenu(
                            expanded = showSuggestions,
                            onDismissRequest = { /* Không làm gì khi click bên ngoài */ },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            suggestions.forEach { account ->
                                DropdownMenuItem(
                                    text = { Text(account.email) },
                                    onClick = {
                                        email = account.email
                                        password = account.password
                                        showSuggestions = false
                                    }
                                )
                            }
                            // Nút đóng gợi ý
                            DropdownMenuItem(
                                text = { Text("Close suggestions") },
                                onClick = { showSuggestions = false }
                            )
                        }
                    } else {
                        showSuggestions = false
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Password field với toggle hiển thị
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, "Password Icon") },
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            "Toggle password visibility"
                        )
                    }
                },
                visualTransformation = if (showPassword) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Login button
            Button(
                onClick = {
                    viewModel.loginWithSavePrompt(email, password, context) { success ->
                        if (success) {
                            // Kiểm tra xem tài khoản đã được lưu chưa
                            if (!accountPreferences.isAccountSaved(email)) {
                                showSaveDialog = true
                            } else {
                                // Nếu đã lưu rồi thì chuyển màn hình luôn
                                onLoginSuccess()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Login", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sign Up Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Don't have an account?", color = Color.Gray)
                TextButton(onClick = { navController.navigate("sign_up") }) {
                    Text("Sign Up")
                }
            }

            // Error message
            loginState.error?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }

        // Loading indicator
        if (loginState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Dialog xác nhận lưu tài khoản
        if (showSaveDialog) {
            AlertDialog(
                onDismissRequest = { /* Không cho phép dismiss bằng cách chạm bên ngoài */ },
                title = { Text("Save Account") },
                text = { Text("Would you like to save this account for future logins?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            accountPreferences.saveAccount(email, password)
                            showSaveDialog = false
                            onLoginSuccess()
                        }
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showSaveDialog = false
                            onLoginSuccess()
                        }
                    ) {
                        Text("No thanks")
                    }
                }
            )
        }
    }
}