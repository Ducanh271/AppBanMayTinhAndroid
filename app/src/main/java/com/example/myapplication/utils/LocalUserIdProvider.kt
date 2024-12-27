package com.example.myapplication.utils

import androidx.compose.runtime.compositionLocalOf

// Khai báo CompositionLocal cho UserID
// tamj thoi chua dung
val LocalUserId = compositionLocalOf<String?> { null }