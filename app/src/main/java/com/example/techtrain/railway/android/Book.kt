package com.example.techtrain.railway.android

import androidx.compose.runtime.Immutable

@Immutable
data class Book(
    val id: String,
    val title: String,
    val url: String,
    val detail: String,
    val review: String,
    val reviewer: String
)
