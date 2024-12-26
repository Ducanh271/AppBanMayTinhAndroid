package com.example.myapplication.utils

import androidx.compose.runtime.compositionLocalOf

// Khai báo CompositionLocal cho UserID
val LocalUserId = compositionLocalOf<String?> { null }